package io.zero88.qwe.micro;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.Functions;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.Status;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.HasSharedData;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.exceptions.DataNotFoundException;
import io.zero88.qwe.exceptions.ServiceNotFoundException;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.client.HttpClientDelegate;
import io.zero88.qwe.micro.MicroConfig.BackendConfig;
import io.zero88.qwe.micro.MicroConfig.ServiceDiscoveryConfig;
import io.zero88.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.zero88.qwe.micro.monitor.ServiceGatewayUsageMonitor;
import io.zero88.qwe.micro.servicetype.EventMessagePusher;
import io.zero88.qwe.micro.servicetype.EventMessageService;
import io.zero88.qwe.micro.type.ServiceKind;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class ServiceDiscoveryInvoker implements Supplier<ServiceDiscovery>, HasSharedData {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryInvoker.class);
    @Getter
    @Accessors(fluent = true)
    private final SharedDataLocalProxy sharedData;
    @Getter(value = AccessLevel.PACKAGE)
    protected final ServiceDiscoveryConfig config;
    private final ServiceDiscovery sd;
    private final CircuitBreakerInvoker cb;
    private final Map<String, Record> registrationMap = new ConcurrentHashMap<>();

    static ServiceDiscovery createServiceDiscovery(Vertx vertx, ServiceDiscoveryConfig config, ServiceKind kind,
                                                   Predicate<Vertx> predicate) {
        if (!config.isEnabled() || !predicate.test(vertx)) {
            logger.info("Skip setup {} Service Discovery", kind);
            return null;
        }
        logger.info("{} Service Discovery Config : {}", kind, config.toJson().encode());
        config.reloadProperty();
        logger.debug("{} Service Discovery | {} | {}", kind, BackendConfig.DEFAULT_SERVICE_DISCOVERY_BACKEND,
                     System.getProperty(BackendConfig.DEFAULT_SERVICE_DISCOVERY_BACKEND));
        return ServiceDiscovery.create(vertx, config);
    }

    abstract <T extends ServiceGatewayAnnounceMonitor> void subscribe(EventBus eventBus, @NonNull T announceMonitor);

    abstract <T extends ServiceGatewayUsageMonitor> void subscribe(EventBus eventBus, @NonNull T usageMonitor);

    abstract ServiceKind kind();

    abstract String computeINet(String host);

    final void subscribe(Vertx vertx, String announceMonitorClass, String usageMonitorClass) {
        subscribe(vertx.eventBus(), ServiceGatewayAnnounceMonitor.create(sharedData, this, announceMonitorClass));
        subscribe(vertx.eventBus(), ServiceGatewayUsageMonitor.create(sharedData, this, usageMonitorClass));
    }

    // TODO: find better way instead force rescan in every register call
    final void rescanService(EventBus eventBus) {
        eventBus.send(config.getAnnounceAddress(), new JsonObject().put("status", Status.UNKNOWN));
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public ServiceDiscovery get() {
        return Objects.requireNonNull(sd, kind() + " Service Discovery is not enabled");
    }

    Future<Void> unregister() {
        if (Objects.isNull(sd)) {
            return Future.succeededFuture();
        }
        return sd.getRecords(r -> registrationMap.containsKey(r.getRegistration()), true)
                 .flatMap(recs -> CompositeFuture.join(
                     recs.stream().map(r -> sd.unpublish(r.getRegistration())).collect(Collectors.toList())))
                 .onFailure(t -> logger.warn("Cannot un-published record", t))
                 .mapEmpty();
    }

    public Future<Record> addRecord(@NonNull Record record) {
        return addDecoratorRecord(decorator(record));
    }

    public Future<Record> addHttpRecord(String name, HttpLocation location, JsonObject metadata) {
        Record record = HttpEndpoint.createRecord(name, location.isSsl(), computeINet(location.getHost()),
                                                  location.getPort(), location.getRoot(), metadata);
        return addDecoratorRecord(record);
    }

    public Future<Record> addEventMessageRecord(String name, String address, EventMethodDefinition definition) {
        return addDecoratorRecord(EventMessageService.createRecord(name, address, definition));
    }

    public Future<Record> addEventMessageRecord(String name, String address, EventMethodDefinition definition,
                                                JsonObject metadata) {
        return addDecoratorRecord(EventMessageService.createRecord(name, address, definition, metadata));
    }

    public Future<ResponseData> executeHttpService(Predicate<Record> filter, String path, HttpMethod method,
                                                   RequestData requestData) {
        return executeHttpService(filter, path, method, requestData, null);
    }

    public Future<ResponseData> executeHttpService(Predicate<Record> filter, String path, HttpMethod method,
                                                   RequestData requestData, HttpClientOptions options) {
        return this.findRecord(filter, HttpEndpoint.TYPE)
                   .map(recs -> recs.stream().findFirst().orElseThrow(ServiceNotFoundException::new))
                   .flatMap(record -> {
                       final JsonObject config = Objects.isNull(options) ? null : options.toJson();
                       ServiceReference ref = get().getReferenceWithConfiguration(record, config);
                       HttpClientDelegate delegate = HttpClientDelegate.create(ref.getAs(HttpClient.class));
                       return cb.wrap(delegate.request(path, method, requestData, false)).eventually(v -> {
                           ref.release();
                           return Future.succeededFuture();
                       });
                   })
                   .onFailure(t -> logger.error("Failed when redirect to {}::{}", method, path, t));
    }

    public Future<ResponseData> executeEventMessageService(Predicate<Record> filter, String path, HttpMethod method,
                                                           RequestData requestData) {
        return executeEventMessageService(filter, path, method, requestData.toJson());
    }

    public Future<ResponseData> executeEventMessageService(Predicate<Record> filter, String path, HttpMethod method,
                                                           JsonObject requestData) {
        return executeEventMessageService(filter, path, method, requestData, null);
    }

    public Future<ResponseData> executeEventMessageService(Predicate<Record> filter, String path, HttpMethod method,
                                                           JsonObject requestData, DeliveryOptions options) {
        //TODO filter by exact path if many paths
        final Comparator<Record> comparator = Comparator.comparingInt(
            r -> EventMethodDefinition.from(r.getMetadata().getJsonObject(EventMessageService.EVENT_METHOD_CONFIG))
                                      .getOrder());
        return this.findRecord(filter, EventMessageService.TYPE)
                   .map(recs -> recs.stream().min(comparator).orElseThrow(ServiceNotFoundException::new))
                   .flatMap(record -> {
                       JsonObject config = new JsonObject().put(EventMessageService.SHARED_KEY_CONFIG,
                                                                sharedData().getSharedKey())
                                                           .put(EventMessageService.DELIVERY_OPTIONS_CONFIG,
                                                                Objects.isNull(options) ? null : options.toJson());
                       ServiceReference ref = get().getReferenceWithConfiguration(record, config);
                       Future<ResponseData> command = ref.getAs(EventMessagePusher.class)
                                                         .execute(path, method, requestData);
                       return cb.wrap(command).eventually(v -> {
                           ref.release();
                           return Future.succeededFuture();
                       });
                   })
                   .onFailure(t -> logger.error("Failed when redirect to {} :: {}", method, path, t));
    }

    private Future<List<Record>> findRecord(Predicate<Record> filter, String type) {
        return get().getRecords(Functions.and(r -> type.equals(r.getType()), filter)::test).flatMap(recs -> {
            if (recs.isEmpty()) {
                return Future.failedFuture(new ServiceNotFoundException("Service Unavailable",
                                                                        new DataNotFoundException(
                                                                            "Not found " + type)));
            }
            return Future.succeededFuture(recs);
        });
    }

    public Future<Boolean> contains(Function<Record, Boolean> filter, String type) {
        return get().getRecord(r -> type.equals(r.getType()) && filter.apply(r)).map(Objects::nonNull);
    }

    public Future<@Nullable Record> get(@NonNull Function<Record, Boolean> filter) {
        return get().getRecord(filter);
    }

    public Future<List<Record>> getRecords() {
        return get().getRecords(r -> true, true);
    }

    public Future<Void> removeRecord(String registration) {
        return get().getRecord(r -> r.getRegistration().equals(registration), true)
                    .map(r -> Optional.ofNullable(r)
                                      .orElseThrow(() -> new ServiceNotFoundException(
                                          "Not found service registration: " + registration)))
                    .onSuccess(r -> registrationMap.remove(r.getRegistration()))
                    .flatMap(record -> get().unpublish(registration));
    }

    private Future<Record> addDecoratorRecord(@NonNull Record record) {
        return get().publish(record).onSuccess(rec -> {
            registrationMap.put(rec.getRegistration(), rec);
            logger.info("Published {} Service | Registration: {} | API: {} | Type: {} | Endpoint: {}", kind(),
                        rec.getRegistration(), rec.getName(), rec.getType(), rec.getLocation().getString("endpoint"));
            if (logger.isTraceEnabled()) {
                logger.trace("Published {} Service: {}", kind(), rec.toJson());
            }
        }).onFailure(t -> logger.error("Cannot publish {} record: {}", kind(), record.toJson(), t));
    }

    private Record decorator(Record record) {
        if (!HttpEndpoint.TYPE.equals(record.getType())) {
            return record;
        }
        HttpLocation location = new HttpLocation(record.getLocation());
        location.setHost(computeINet(location.getHost()));
        return record.setLocation(location.toJson());
    }

}

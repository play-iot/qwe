package io.github.zero88.msa.bp.micro;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.github.zero88.msa.bp.dto.msg.RequestData;
import io.github.zero88.msa.bp.dto.msg.ResponseData;
import io.github.zero88.msa.bp.exceptions.NotFoundException;
import io.github.zero88.msa.bp.http.client.HttpClientDelegate;
import io.github.zero88.msa.bp.micro.MicroConfig.BackendConfig;
import io.github.zero88.msa.bp.micro.MicroConfig.ServiceDiscoveryConfig;
import io.github.zero88.msa.bp.micro.metadata.EventMethodDefinition;
import io.github.zero88.msa.bp.micro.metadata.ServiceNotFoundException;
import io.github.zero88.msa.bp.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.github.zero88.msa.bp.micro.monitor.ServiceGatewayUsageMonitor;
import io.github.zero88.msa.bp.micro.type.EventMessagePusher;
import io.github.zero88.msa.bp.micro.type.EventMessageService;
import io.github.zero88.utils.Functions;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.Status;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.HttpLocation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class ServiceDiscoveryController implements Supplier<ServiceDiscovery> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryController.class);
    @Getter(value = AccessLevel.PACKAGE)
    protected final ServiceDiscoveryConfig config;
    private final String sharedKey;
    private final ServiceDiscovery serviceDiscovery;
    private final CircuitBreakerController circuitController;
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
        subscribe(vertx.eventBus(), ServiceGatewayAnnounceMonitor.create(vertx, this, sharedKey, announceMonitorClass));
        subscribe(vertx.eventBus(), ServiceGatewayUsageMonitor.create(vertx, this, sharedKey, usageMonitorClass));
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
        return Objects.requireNonNull(serviceDiscovery, kind() + " Service Discovery is not enabled");
    }

    void unregister(Promise<Void> promise) {
        if (Objects.nonNull(serviceDiscovery)) {
            io.vertx.reactivex.servicediscovery.ServiceDiscovery serviceDiscovery = getRx();
            serviceDiscovery.rxGetRecords(r -> registrationMap.containsKey(r.getRegistration()), true)
                            .flattenAsObservable(rs -> rs)
                            .flatMapCompletable(r -> serviceDiscovery.rxUnpublish(r.getRegistration()))
                            .subscribe(promise::complete, err -> {
                                logger.warn("Cannot un-published record", err);
                                promise.complete();
                            });
        }
    }

    public Single<Record> addRecord(@NonNull Record record) {
        return addDecoratorRecord(decorator(record));
    }

    public Single<Record> addHttpRecord(String name, HttpLocation location, JsonObject metadata) {
        Record record = HttpEndpoint.createRecord(name, location.isSsl(), computeINet(location.getHost()),
                                                  location.getPort(), location.getRoot(), metadata);
        return addDecoratorRecord(record);
    }

    public Single<Record> addEventMessageRecord(String name, String address, EventMethodDefinition definition) {
        return addDecoratorRecord(EventMessageService.createRecord(name, address, definition));
    }

    public Single<Record> addEventMessageRecord(String name, String address, EventMethodDefinition definition,
                                                JsonObject metadata) {
        return addDecoratorRecord(EventMessageService.createRecord(name, address, definition, metadata));
    }

    public Single<ResponseData> executeHttpService(Predicate<Record> filter, String path, HttpMethod method,
                                                   RequestData requestData) {
        return executeHttpService(filter, path, method, requestData, null);
    }

    public Single<ResponseData> executeHttpService(Predicate<Record> filter, String path, HttpMethod method,
                                                   RequestData requestData, HttpClientOptions options) {
        return findRecord(filter, HttpEndpoint.TYPE).firstOrError().flatMap(record -> {
            ServiceReference reference = get().getReferenceWithConfiguration(record, Objects.isNull(options)
                                                                                     ? null
                                                                                     : options.toJson());
            HttpClientDelegate delegate = HttpClientDelegate.create(reference.getAs(HttpClient.class));
            return circuitController.wrap(delegate.request(path, method, requestData, false))
                                    .doFinally(reference::release);
        }).doOnError(t -> logger.error("Failed when redirect to {}::{}", t, method, path));
    }

    public Single<ResponseData> executeEventMessageService(Predicate<Record> filter, String path, HttpMethod method,
                                                           RequestData requestData) {
        return executeEventMessageService(filter, path, method, requestData.toJson());
    }

    public Single<ResponseData> executeEventMessageService(Predicate<Record> filter, String path, HttpMethod method,
                                                           JsonObject requestData) {
        return executeEventMessageService(filter, path, method, requestData, null);
    }

    public Single<ResponseData> executeEventMessageService(Predicate<Record> filter, String path, HttpMethod method,
                                                           JsonObject requestData, DeliveryOptions options) {
        //TODO filter by exact path if many paths
        final Comparator<Record> comparator = Comparator.comparingInt(
            r -> EventMethodDefinition.from(r.getMetadata().getJsonObject(EventMessageService.EVENT_METHOD_CONFIG))
                                      .getOrder());
        return findRecord(filter, EventMessageService.TYPE).sorted(comparator).firstOrError().flatMap(record -> {
            JsonObject config = new JsonObject().put(EventMessageService.SHARED_KEY_CONFIG, sharedKey)
                                                .put(EventMessageService.DELIVERY_OPTIONS_CONFIG,
                                                     Objects.isNull(options) ? new JsonObject() : options.toJson());
            ServiceReference ref = get().getReferenceWithConfiguration(record, config);
            Single<ResponseData> command = Single.create(source -> ref.getAs(EventMessagePusher.class)
                                                                      .execute(path, method, requestData,
                                                                               source::onSuccess, source::onError));
            return circuitController.wrap(command).doFinally(ref::release);
        }).doOnError(t -> logger.error("Failed when redirect to {} :: {}", t, method, path));
    }

    private Observable<Record> findRecord(Predicate<Record> filter, String type) {
        return getRx().rxGetRecords(Functions.and(r -> type.equals(r.getType()), filter)::test)
                      .filter(list -> !list.isEmpty())
                      .switchIfEmpty(Single.error(new ServiceNotFoundException("Service Unavailable",
                                                                               new NotFoundException(
                                                                                   "Not found " + type))))
                      .flattenAsObservable(r -> r);
    }

    public Single<Boolean> contains(Function<Record, Boolean> filter, String type) {
        return getRx().rxGetRecord(r -> type.equals(r.getType()) && filter.apply(r)).count().map(c -> c > 0);
    }

    public Maybe<Record> get(@NonNull Function<Record, Boolean> filter) {
        return getRx().rxGetRecord(filter);
    }

    public Single<List<Record>> getRecords() {
        return getRx().rxGetRecords(r -> true, true);
    }

    public Completable removeRecord(String registration) {
        return getRx().rxGetRecord(r -> r.getRegistration().equals(registration), true)
                      .switchIfEmpty(Single.error(new ServiceNotFoundException("Not found that registration")))
                      .flatMapCompletable(record -> {
                          registrationMap.remove(registration);
                          return getRx().rxUnpublish(registration);
                      });
    }

    private Single<Record> addDecoratorRecord(@NonNull Record record) {
        return getRx().rxPublish(record).doOnSuccess(rec -> {
            registrationMap.put(rec.getRegistration(), rec);
            logger.info("Published {} Service | Registration: {} | API: {} | Type: {} | Endpoint: {}", kind(),
                        rec.getRegistration(), rec.getName(), rec.getType(), rec.getLocation().getString("endpoint"));
            if (logger.isTraceEnabled()) {
                logger.trace("Published {} Service: {}", kind(), rec.toJson());
            }
        }).doOnError(t -> logger.error("Cannot publish {} record: {}", t, kind(), record.toJson()));
    }

    private Record decorator(Record record) {
        if (!HttpEndpoint.TYPE.equals(record.getType())) {
            return record;
        }
        HttpLocation location = new HttpLocation(record.getLocation());
        location.setHost(computeINet(location.getHost()));
        return record.setLocation(location.toJson());
    }

    io.vertx.reactivex.servicediscovery.ServiceDiscovery getRx() {
        return io.vertx.reactivex.servicediscovery.ServiceDiscovery.newInstance(get());
    }

}

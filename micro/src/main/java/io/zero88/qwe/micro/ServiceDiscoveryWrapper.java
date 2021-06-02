package io.zero88.qwe.micro;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
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
import io.zero88.qwe.micro.MicroConfig.ServiceDiscoveryConfig;
import io.zero88.qwe.micro.servicetype.EventMessageHttpService;
import io.zero88.qwe.micro.servicetype.EventMessagePusher;
import io.zero88.qwe.utils.Networks;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public final class ServiceDiscoveryWrapper implements Supplier<ServiceDiscovery>, HasSharedData {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryWrapper.class);
    @Getter
    @Accessors(fluent = true)
    private final SharedDataLocalProxy sharedData;
    private final ServiceDiscovery sd;
    @Getter(value = AccessLevel.PACKAGE)
    private final CircuitBreakerWrapper cb;

    ServiceDiscoveryWrapper(SharedDataLocalProxy sharedData, ServiceDiscoveryConfig config, CircuitBreakerWrapper cb) {
        this.sharedData = sharedData;
        this.cb = cb;
        this.sd = ServiceDiscovery.create(sharedData.getVertx(), config);
    }

    String computeINet(String host) {
        return Networks.computeNATAddress(host);
    }

    @Override
    public ServiceDiscovery get() {
        return Objects.requireNonNull(sd, "Service Discovery is not enabled");
    }

    Future<Void> unregister() {
        if (Objects.isNull(sd)) {
            return Future.succeededFuture();
        }
        return sd.getRecords(r -> true, true)
                 .flatMap(recs -> CompositeFuture.join(
                     recs.stream().map(r -> sd.unpublish(r.getRegistration())).collect(Collectors.toList())))
                 .onFailure(t -> logger.warn("Cannot un-published record(s)", t))
                 .mapEmpty();
    }

    public Future<Record> addRecord(@NonNull Record record) {
        return addDecoratorRecord(decorator(record));
    }

    public Future<Record> addRecord(String name, HttpLocation location, JsonObject metadata) {
        Record record = HttpEndpoint.createRecord(name, location.isSsl(), computeINet(location.getHost()),
                                                  location.getPort(), location.getRoot(), metadata);
        return addDecoratorRecord(record);
    }

    public Future<Record> addRecord(String name, String address, EventMethodDefinition definition) {
        return addDecoratorRecord(EventMessageHttpService.createRecord(name, address, definition));
    }

    public Future<Record> addRecord(String name, String address, EventMethodDefinition definition,
                                    JsonObject metadata) {
        return addDecoratorRecord(EventMessageHttpService.createRecord(name, address, definition, metadata));
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
                   .onFailure(t -> logger.error("Failed when redirect [{}::{}]", method, path, t));
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
        Comparator<Record> c = Comparator.comparingInt(r -> EventMethodDefinition.from(r.getLocation()).getOrder());
        return this.findRecord(filter, EventMessageHttpService.TYPE)
                   .map(recs -> recs.stream().min(c).orElseThrow(ServiceNotFoundException::new))
                   .flatMap(record -> {
                       JsonObject config = new JsonObject().put(EventMessageHttpService.SHARED_KEY_CONFIG,
                                                                sharedData().getSharedKey())
                                                           .put(EventMessageHttpService.DELIVERY_OPTIONS_CONFIG,
                                                                Objects.isNull(options) ? null : options.toJson());
                       ServiceReference ref = get().getReferenceWithConfiguration(record, config);
                       Future<ResponseData> command = ref.getAs(EventMessagePusher.class)
                                                         .execute(path, method, requestData);
                       return cb.wrap(command).eventually(v -> {
                           ref.release();
                           return Future.succeededFuture();
                       });
                   })
                   .onFailure(t -> logger.error("Failed when redirect to [{}::{}]", method, path, t));
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

    public Future<@Nullable Record> find(@NonNull Function<Record, Boolean> filter) {
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
                    .flatMap(record -> get().unpublish(registration));
    }

    private Future<Record> addDecoratorRecord(@NonNull Record record) {
        return get().publish(record).onSuccess(rec -> {
            logger.info("Published Service | Registration[{}] | API[{}] | Type[{}] | Endpoint[{}]",
                        rec.getRegistration(), rec.getName(), rec.getType(), rec.getLocation().getString("endpoint"));
            if (logger.isTraceEnabled()) {
                logger.trace("Published Service [{}]", rec.toJson());
            }
        }).onFailure(t -> logger.error("Cannot publish record[{}]", record.toJson(), t));
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

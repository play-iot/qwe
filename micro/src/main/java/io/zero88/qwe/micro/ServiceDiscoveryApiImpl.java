package io.zero88.qwe.micro;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.exceptions.ServiceException;
import io.zero88.qwe.exceptions.ServiceNotFoundException;
import io.zero88.qwe.exceptions.ServiceUnavailable;
import io.zero88.qwe.micro.filter.PredicateFactoryLoader;
import io.zero88.qwe.micro.filter.RecordPredicateFactory.SearchFlag;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

final class ServiceDiscoveryApiImpl implements ServiceDiscoveryApi {

    @Getter
    @Accessors(fluent = true)
    private final SharedDataLocalProxy sharedData;
    private final ServiceDiscovery sd;
    @Getter(value = AccessLevel.PACKAGE)
    private final CircuitBreakerWrapper cb;
    private final PredicateFactoryLoader predicateLoader;
    private final ServiceExecutorFactory executorFactory;

    ServiceDiscoveryApiImpl(SharedDataLocalProxy sharedData, ServiceDiscoveryConfig config, CircuitBreakerWrapper cb) {
        this.sharedData = sharedData;
        this.cb = cb;
        this.sd = ServiceDiscovery.create(sharedData.getVertx(), config);
        this.predicateLoader = new PredicateFactoryLoader();
        this.executorFactory = new ServiceExecutorFactory();
    }

    @Override
    public ServiceDiscovery get() {
        return Objects.requireNonNull(sd, "Service Discovery is not enabled");
    }

    public Future<Record> register(@NonNull Record record) {
        return addDecoratorRecord(decorator(record));
    }

    @Override
    public CompositeFuture register(@NonNull List<Record> records) {
        return CompositeFuture.join(records.stream().map(this::register).collect(Collectors.toList()));
    }

    @Override
    public Future<Record> update(@NonNull Record record) {
        return Future.failedFuture("Not yet implemented");
    }

    @Override
    public Future<Record> updateMany(@NonNull RequestFilter filter, @NonNull JsonObject updateData) {
        return Future.failedFuture("Not yet implemented");
    }

    @Override
    public Future<Void> unregister(@NonNull RequestFilter filter) {
        return Future.succeededFuture();
    }

    @Override
    public Future<@Nullable Record> findOne(@NonNull RequestFilter filter) {
        return find(filter, SearchFlag.ONE).map(recs -> recs.stream().reduce((m1, m2) -> {
            if (m1.equals(m2)) {
                throw new ServiceException("More than one service by given parameters [" + filter + "]");
            }
            return m1;
        }).orElseThrow(() -> new ServiceNotFoundException("Not found service by given parameters [" + filter + "]")));
    }

    @Override
    public Future<List<Record>> findMany(@NonNull RequestFilter filter) {
        return find(filter, SearchFlag.MANY);
    }

    @Override
    public Future<ResponseData> execute(@NonNull RequestFilter filter, RequestData requestData) {
        return this.findOne(filter)
                   .flatMap(r -> Optional.ofNullable(executorFactory.lookup(r.getType()))
                                         .map(s -> execute(r, s, requestData))
                                         .orElseThrow(() -> new ServiceUnavailable("Unable execute service")));
    }

    public Future<Void> unregister(String registration) {
        return get().getRecord(r -> r.getRegistration().equals(registration), true)
                    .map(r -> Optional.ofNullable(r)
                                      .orElseThrow(() -> new ServiceNotFoundException(
                                          "Not found service registration: " + registration)))
                    .flatMap(record -> get().unpublish(registration));
    }

    private Future<Record> addDecoratorRecord(@NonNull Record record) {
        return get().publish(record).onSuccess(rec -> {
            logger().info("Published Service | Registration[{}] | API[{}] | Type[{}] | Endpoint[{}]",
                          rec.getRegistration(), rec.getName(), rec.getType(), rec.getLocation().getString("endpoint"));
            if (logger().isTraceEnabled()) {
                logger().trace("Published Service [{}]", rec.toJson());
            }
        }).onFailure(t -> logger().error("Cannot publish record[{}]", record.toJson(), t));
    }

    private Future<List<Record>> find(RequestFilter filter, SearchFlag searchFlag) {
        logger().debug("Lookup by filter [{}][{}]", searchFlag, filter.toJson());
        return get().getRecords(predicateLoader.getPredicatesFactories()
                                               .stream()
                                               .map(p -> p.apply(filter, searchFlag))
                                               .reduce(Predicate::and)
                                               .orElseGet(() -> r -> false)::test);
    }

    private Record decorator(Record record) {
        if (!HttpEndpoint.TYPE.equals(record.getType())) {
            return record;
        }
        HttpLocation loc = new HttpLocation(record.getLocation());
        if (Objects.nonNull(loc.getHost())) {
            return record;
        }
        return record.setLocation(loc.setHost(sharedData().getData(SharedDataLocalProxy.PUBLIC_IPV4_KEY)).toJson());
    }

    private Future<ResponseData> execute(@Nullable Record record, ServiceExecutor serviceExecutor,
                                         RequestData requestData) {
        final JsonObject configuration = serviceExecutor.getConfiguration(record, sharedData());
        final ServiceReference ref = get().getReferenceWithConfiguration(record, configuration);
        return serviceExecutor.execute(ref, requestData).eventually(ignore -> {
            ref.release();
            return Future.succeededFuture();
        });
    }

    private Future<Void> unregister() {
        if (Objects.isNull(sd)) {
            return Future.succeededFuture();
        }
        return sd.getRecords(r -> true, true)
                 .flatMap(recs -> CompositeFuture.join(
                     recs.stream().map(r -> sd.unpublish(r.getRegistration())).collect(Collectors.toList())))
                 .onFailure(t -> logger().warn("Cannot un-published record(s)", t))
                 .mapEmpty();
    }

}

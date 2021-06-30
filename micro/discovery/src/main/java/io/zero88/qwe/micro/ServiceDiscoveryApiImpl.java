package io.zero88.qwe.micro;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.github.zero88.utils.Strings;
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
import io.zero88.qwe.exceptions.ServiceNotFoundException;
import io.zero88.qwe.exceptions.ServiceUnavailable;
import io.zero88.qwe.micro.filter.PredicateFactoryLoader;
import io.zero88.qwe.micro.filter.RecordPredicateFactory.SearchFlag;
import io.zero88.qwe.micro.filter.ServiceFilterParam;

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
    public Future<List<Record>> register(@NonNull List<Record> records) {
        return CompositeFuture.join(records.stream().map(this::register).collect(Collectors.toList()))
                              .flatMap(ar -> Future.succeededFuture(ar.list()));
    }

    @Override
    public Future<Record> update(@NonNull Record record) {
        return this.findOne(new RequestFilter().put(ServiceFilterParam.IDENTIFIER,
                                                    Strings.requireNotBlank(record.getRegistration(),
                                                                            "Missing record registration")))
                   .flatMap(r -> doUpdate(r, record));
    }

    @Override
    public Future<List<Record>> batchUpdate(@NonNull RequestFilter filter, @NonNull JsonObject updateData) {
        final Record r = new Record(updateData);
        return findMany(filter).map(recs -> CompositeFuture.join(batchUpdate(r, recs)))
                               .flatMap(ar -> Future.succeededFuture(ar.list()));
    }

    @Override
    public Future<Void> unregister(@NonNull RequestFilter filter) {
        return findMany(filter).flatMap(recs -> CompositeFuture.join(
            recs.stream().map(r -> get().unpublish(r.getRegistration())).collect(Collectors.toList()))).mapEmpty();
    }

    @Override
    public Future<Record> findOne(@NonNull RequestFilter filter) {
        return find(filter, SearchFlag.ONE).map(recs -> recs.stream().reduce((m1, m2) -> {
            if (!m1.equals(m2)) {
                throw new IllegalArgumentException("More than one service by given parameters [" + filter + "]");
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
        return predicateLoader.getPredicatesFactories()
                              .stream()
                              .map(p -> p.apply(filter, searchFlag))
                              .reduce(Predicate::and)
                              .map(predicate -> get().getRecords(predicate::test, true))
                              .orElseGet(() -> get().getRecords(filter));
    }

    private List<Future> batchUpdate(Record c, List<Record> recs) {
        return recs.stream().map(r -> doUpdate(r, c)).collect(Collectors.toList());
    }

    private Future<Record> doUpdate(Record prev, Record current) {
        return get().update(new Record(prev.toJson().mergeIn(current.toJson(), true)));
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

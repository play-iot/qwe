package io.zero88.qwe.micro;

import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.dto.msg.Filters;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.exceptions.ServiceNotFoundException;
import io.zero88.qwe.micro.filter.RecordPredicateFactory;
import io.zero88.qwe.micro.filter.ServiceLocatorParams;
import io.zero88.qwe.micro.transfomer.RecordTransformer.ViewType;
import io.zero88.qwe.micro.transfomer.RecordTransformerLoader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ServiceLocator implements EventListener {

    @NonNull
    private final ServiceDiscoveryWrapper discovery;

    @EBContract(action = "GET_ONE")
    public Future<JsonObject> get(RequestData reqData) {
        final RequestFilter filter = getFilter(reqData);
        final ViewType view = ViewType.parse((String) filter.remove(ServiceLocatorParams.VIEW));
        filter.remove(Filters.PRETTY);
        filter.put(ServiceLocatorParams.IDENTIFIER, Optional.ofNullable(reqData)
                                                            .flatMap(req -> Optional.ofNullable(req.body()))
                                                            .map(b -> b.getString(ServiceLocatorParams.IDENTIFIER))
                                                            .orElse(null));
        logger().debug("Lookup by filter [{}]", filter.toJson());
        return discovery.find(RecordPredicateFactory.filter(filter, EventAction.GET_ONE))
                        .map(r -> Optional.ofNullable(r)
                                          .orElseThrow(() -> new ServiceNotFoundException(
                                              "Not found service by given parameters [" + filter + "]")))
                        .map(r -> transform(r, view));
    }

    @EBContract(action = "GET_LIST")
    public Future<JsonObject> list(RequestData reqData) {
        final RequestFilter filter = getFilter(reqData);
        final ViewType view = ViewType.parse((String) filter.remove(ServiceLocatorParams.VIEW));
        filter.remove(Filters.PRETTY);
        logger().debug("Lookup by filter [{}]", filter.toJson());
        return discovery.get()
                        .getRecords(RecordPredicateFactory.filter(filter, EventAction.GET_LIST))
                        .map(records -> records.stream().map(r -> transform(r, view)).collect(Collectors.toList()))
                        .map(records -> new JsonObject().put("apis", records));
    }

    private RequestFilter getFilter(RequestData reqData) {
        return new RequestFilter(Optional.ofNullable(reqData).map(RequestData::filter).orElseGet(RequestFilter::new));
    }

    private JsonObject transform(Record record, ViewType view) {
        return RecordTransformerLoader.instance().lookup(record.getType(), view).transform(record).toJson();
    }

}

package io.zero88.qwe.micro;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.micro.filter.ServiceFilterParam;
import io.zero88.qwe.micro.transfomer.RecordTransformer.ViewType;
import io.zero88.qwe.micro.transfomer.RecordTransformerLoader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceLocator implements EventListener {

    public static final String APIS_KEY = "apis";
    @NonNull
    private final ServiceDiscoveryApi discovery;

    @EBContract(action = "GET_ONE")
    public Future<JsonObject> get(RequestData reqData) {
        final RequestFilter filter = parseFilter(reqData);
        final ViewType view = ViewType.parse((String) filter.remove(ServiceFilterParam.VIEW));
        filter.put(ServiceFilterParam.IDENTIFIER, Optional.ofNullable(reqData)
                                                          .flatMap(req -> Optional.ofNullable(req.body()))
                                                          .map(b -> b.getString(ServiceFilterParam.IDENTIFIER))
                                                          .orElse(null));
        return discovery.findOne(filter).map(r -> transform(r, view));
    }

    @EBContract(action = "GET_LIST")
    public Future<JsonObject> list(RequestData reqData) {
        final RequestFilter filter = parseFilter(reqData);
        return collect(discovery.findMany(filter), ViewType.parse(filter.getString(ServiceFilterParam.VIEW)));
    }

    @EBContract(action = "CREATE")
    public Future<JsonObject> create(RequestData reqData) {
        final RequestFilter filter = parseFilter(reqData);
        final ViewType view = ViewType.parse(filter.getString(ServiceFilterParam.VIEW));
        final JsonObject body = Optional.ofNullable(reqData.body()).orElseGet(JsonObject::new);
        if (body.containsKey(APIS_KEY)) {
            return collect(discovery.register(body.getJsonArray(APIS_KEY)
                                                  .stream()
                                                  .filter(JsonObject.class::isInstance)
                                                  .map(r -> new Record((JsonObject) r))
                                                  .collect(Collectors.toList())), view);
        } else {
            return discovery.register(new Record(body)).map(r -> transform(r, view));
        }
    }

    @EBContract(action = "UPDATE")
    public Future<JsonObject> update(RequestData req) {
        return discovery.update(new Record(req.body()))
                        .map(r -> transform(r, ViewType.parse(parseFilter(req).getString(ServiceFilterParam.VIEW))));
    }

    @EBContract(action = "BATCH_UPDATE")
    public Future<JsonObject> batchUpdate(RequestData reqData) {
        final RequestFilter filter = parseFilter(reqData);
        return collect(discovery.batchUpdate(filter, reqData.body()),
                       ViewType.parse(filter.getString(ServiceFilterParam.VIEW)));
    }

    @EBContract(action = "REMOVE")
    public Future<JsonObject> remove(RequestData reqData) {
        return discovery.unregister(parseFilter(reqData));
    }

    private RequestFilter parseFilter(RequestData reqData) {
        return Optional.ofNullable(reqData)
                       .map(RequestData::filter)
                       .map(RequestFilter::new)
                       .orElseGet(RequestFilter::new)
                       .getAppFilter();
    }

    private JsonObject transform(Record record, ViewType view) {
        return RecordTransformerLoader.instance().lookup(record.getType(), view).transform(record).toJson();
    }

    private Future<JsonObject> collect(Future<List<Record>> records, ViewType viewType) {
        return records.map(rr -> rr.stream().map(r -> transform(r, viewType)).collect(Collectors.toList()))
                      .map(rr -> new JsonObject().put(APIS_KEY, rr));
    }

}

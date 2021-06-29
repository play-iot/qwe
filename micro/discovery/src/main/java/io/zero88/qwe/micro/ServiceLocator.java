package io.zero88.qwe.micro;

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
        final ViewType view = ViewType.parse((String) filter.remove(ServiceFilterParam.VIEW));
        return discovery.findMany(filter)
                        .map(records -> records.stream().map(r -> transform(r, view)).collect(Collectors.toList()))
                        .map(records -> new JsonObject().put("apis", records));
    }

    @EBContract(action = "CREATE")
    public Future<JsonObject> create(RequestData reqData) {
        final RequestFilter filter = parseFilter(reqData);
        final ViewType view = ViewType.parse((String) filter.remove(ServiceFilterParam.VIEW));
        return discovery.register(new Record(reqData.body())).map(r -> transform(r, view));
    }

    @EBContract(action = "UPDATE")
    public Future<JsonObject> update(RequestData reqData) {
        final RequestFilter filter = parseFilter(reqData);
        return discovery.update(new Record()).map(Record::toJson);
    }

    @EBContract(action = "REMOVE")
    public Future<JsonObject> remove(RequestData reqData) {
        return discovery.unregister(parseFilter(reqData)).map(ignore -> new JsonObject());
    }

    private RequestFilter parseFilter(RequestData reqData) {
        return new RequestFilter(
            Optional.ofNullable(reqData).map(RequestData::filter).orElseGet(RequestFilter::new)).getAppFilter();
    }

    private JsonObject transform(Record record, ViewType view) {
        return RecordTransformerLoader.instance().lookup(record.getType(), view).transform(record).toJson();
    }

}

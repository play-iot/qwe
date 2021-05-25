package io.zero88.qwe.micro;

import java.util.Optional;
import java.util.stream.Collectors;

import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.Filters;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.exceptions.ServiceNotFoundException;
import io.zero88.qwe.micro.filter.RecordPredicate;
import io.zero88.qwe.micro.filter.ServiceLocatorParams;
import io.zero88.qwe.micro.transfomer.RecordOutput;
import io.zero88.qwe.micro.transfomer.RecordTransformer;
import io.zero88.qwe.micro.transfomer.RecordTransformer.RecordView;
import io.zero88.qwe.micro.type.ServiceKind;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ServiceLocator implements EventListener {

    @NonNull
    private final MicroContext context;

    @EBContract(action = "GET_ONE")
    public Future<JsonObject> get(@NonNull RequestData reqData) {
        final RequestFilter filter = new RequestFilter(reqData.filter());
        filter.remove(Filters.PRETTY);
        final ServiceDiscoveryInvoker controller = getController(filter);
        final RecordView view = RecordView.parse((String) filter.remove(ServiceLocatorParams.VIEW));
        final String identifier = Optional.ofNullable(reqData.body())
                                          .map(body -> body.getString(ServiceLocatorParams.IDENTIFIER))
                                          .orElse("");
        filter.put(ServiceLocatorParams.IDENTIFIER, Strings.requireNotBlank(identifier, "Missing record identifier"));
        return controller.get()
                         .getRecord(RecordPredicate.filter(filter, EventAction.GET_ONE))
                         .map(r -> Optional.ofNullable(r)
                                           .orElseThrow(() -> new ServiceNotFoundException(
                                               "Not found service by given parameters: " + filter)))
                         .map(RecordTransformer.create(view)::transform)
                         .map(RecordOutput::toJson);
    }

    @EBContract(action = "GET_LIST")
    public Future<JsonObject> list(@NonNull RequestData requestData) {
        final RequestFilter filter = new RequestFilter(requestData.filter());
        filter.remove(Filters.PRETTY);
        ServiceDiscoveryInvoker controller = getController(filter);
        RecordTransformer transformer = RecordTransformer.create(RecordView.END_USER);
        return controller.get()
                         .getRecords(RecordPredicate.filter(filter, EventAction.GET_LIST))
                         .map(records -> records.stream()
                                                .map(transformer::transform)
                                                .map(RecordOutput::toJson)
                                                .collect(Collectors.toList()))
                         .map(records -> new JsonObject().put("apis", records));
    }

    private ServiceDiscoveryInvoker getController(JsonObject filter) {
        ServiceKind scope = ServiceKind.parse((String) filter.remove(ServiceLocatorParams.KIND));
        return ServiceKind.LOCAL == scope ? context.getLocalInvoker() : context.getClusterInvoker();
    }

}

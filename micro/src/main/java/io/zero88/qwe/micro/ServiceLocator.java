package io.zero88.qwe.micro;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import io.zero88.qwe.dto.msg.Filters;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventContractor;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.micro.filter.ServiceLocatorParams;
import io.zero88.qwe.micro.filter.RecordPredicate;
import io.zero88.qwe.micro.type.ServiceKind;
import io.zero88.qwe.micro.transfomer.RecordOutput;
import io.zero88.qwe.micro.transfomer.RecordTransformer;
import io.zero88.qwe.micro.transfomer.RecordTransformer.RecordView;
import io.github.zero88.utils.Strings;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ServiceLocator implements EventListener {

    @NonNull
    private final MicroContext context;

    @Override
    public @NonNull Collection<EventAction> getAvailableEvents() {
        return Arrays.asList(EventAction.GET_ONE, EventAction.GET_LIST);
    }

    @EventContractor(action = "GET_ONE", returnType = Single.class)
    public Single<JsonObject> get(@NonNull RequestData requestData) {
        final RequestFilter filter = new RequestFilter(requestData.filter());
        filter.remove(Filters.PRETTY);
        final ServiceDiscoveryInvoker controller = getController(filter);
        final RecordView view = RecordView.parse((String) filter.remove(ServiceLocatorParams.VIEW));
        final String identifier = Optional.ofNullable(requestData.body())
                                          .map(body -> body.getString(ServiceLocatorParams.IDENTIFIER))
                                          .orElse("");
        filter.put(ServiceLocatorParams.IDENTIFIER, Strings.requireNotBlank(identifier, "Missing record identifier"));
        return controller.getRx()
                         .rxGetRecord(RecordPredicate.filter(filter, EventAction.GET_ONE))
                         .map(RecordTransformer.create(view)::transform)
                         .map(RecordOutput::toJson)
                         .switchIfEmpty(Single.error(new ServiceNotFoundException(
                             "Not found service by given parameters: " +
                             requestData.filter().put(ServiceLocatorParams.IDENTIFIER, identifier).encode())));
    }

    @EventContractor(action = "GET_LIST", returnType = Single.class)
    public Single<JsonObject> list(@NonNull RequestData requestData) {
        final RequestFilter filter = new RequestFilter(requestData.filter());
        filter.remove(Filters.PRETTY);
        ServiceDiscoveryInvoker controller = getController(filter);
        RecordTransformer transformer = RecordTransformer.create(RecordView.END_USER);
        return controller.getRx()
                         .rxGetRecords(RecordPredicate.filter(filter, EventAction.GET_LIST))
                         .flatMapObservable(records -> Observable.fromIterable(records).map(transformer::transform))
                         .map(RecordOutput::toJson)
                         .collect(JsonArray::new, JsonArray::add)
                         .map(records -> new JsonObject().put("apis", records));
    }

    private ServiceDiscoveryInvoker getController(JsonObject filter) {
        ServiceKind scope = ServiceKind.parse((String) filter.remove(ServiceLocatorParams.KIND));
        return ServiceKind.LOCAL == scope ? context.getLocalInvoker() : context.getClusterInvoker();
    }

}

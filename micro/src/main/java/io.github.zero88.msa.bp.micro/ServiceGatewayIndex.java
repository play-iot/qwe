package io.github.zero88.msa.bp.micro;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import io.github.zero88.msa.bp.dto.msg.Filters;
import io.github.zero88.msa.bp.dto.msg.RequestData;
import io.github.zero88.msa.bp.dto.msg.RequestFilter;
import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventContractor;
import io.github.zero88.msa.bp.event.EventListener;
import io.github.zero88.msa.bp.exceptions.ServiceNotFoundException;
import io.github.zero88.msa.bp.micro.filter.RecordPredicate;
import io.github.zero88.msa.bp.micro.transfomer.RecordOutput;
import io.github.zero88.msa.bp.micro.transfomer.RecordTransformer;
import io.github.zero88.msa.bp.micro.transfomer.RecordTransformer.RecordView;
import io.github.zero88.utils.Strings;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ServiceGatewayIndex implements EventListener {

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
        final ServiceDiscoveryController controller = getController(filter);
        final RecordView view = RecordView.parse((String) filter.remove(Params.VIEW));
        final String identifier = Optional.ofNullable(requestData.body())
                                          .map(body -> body.getString(Params.IDENTIFIER))
                                          .orElse(null);
        filter.put(Params.IDENTIFIER, Strings.requireNotBlank(identifier, "Missing record identifier"));
        return controller.getRx()
                         .rxGetRecord(RecordPredicate.filter(filter, EventAction.GET_ONE))
                         .map(RecordTransformer.create(view)::transform)
                         .map(RecordOutput::toJson)
                         .switchIfEmpty(Single.error(new ServiceNotFoundException(
                             "Not found service by given parameters: " +
                             requestData.filter().put(Params.IDENTIFIER, identifier).encode())));
    }

    @EventContractor(action = "GET_LIST", returnType = Single.class)
    public Single<JsonObject> list(@NonNull RequestData requestData) {
        final RequestFilter filter = new RequestFilter(requestData.filter());
        filter.remove(Filters.PRETTY);
        ServiceDiscoveryController controller = getController(filter);
        RecordTransformer transformer = RecordTransformer.create(RecordView.END_USER);
        return controller.getRx()
                         .rxGetRecords(RecordPredicate.filter(filter, EventAction.GET_LIST))
                         .flatMapObservable(records -> Observable.fromIterable(records).map(transformer::transform))
                         .map(RecordOutput::toJson)
                         .collect(JsonArray::new, JsonArray::add)
                         .map(records -> new JsonObject().put("apis", records));
    }

    private ServiceDiscoveryController getController(JsonObject filter) {
        ServiceKind scope = ServiceKind.parse((String) filter.remove(Params.KIND));
        return ServiceKind.LOCAL == scope ? context.getLocalController() : context.getClusterController();
    }

    public static final class Params {

        public static final String IDENTIFIER = "identifier";
        public static final String TYPE = "_type";
        public static final String STATUS = "_status";
        public static final String SCOPE = "_scope";
        public static final String BY = "_by";
        public static final String VIEW = "_view";
        public static final String KIND = "_kind";

        public static final String ACTION = "_action";

    }

}

package io.github.zero88.msa.bp.http.server.handler;

import java.util.function.Predicate;

import io.github.zero88.msa.bp.dto.ErrorMessage;
import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.dto.msg.ResponseData;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.http.event.EventMethodDefinition;
import io.github.zero88.msa.bp.http.server.converter.RequestDataConverter;
import io.github.zero88.msa.bp.http.server.handler.DynamicContextDispatcher.AbstractDynamicContextDispatcher;
import io.github.zero88.msa.bp.http.server.rest.DynamicEventRestApi;
import io.github.zero88.msa.bp.micro.ServiceDiscoveryController;
import io.github.zero88.msa.bp.micro.type.EventMessageService;
import io.github.zero88.utils.Functions;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.servicediscovery.Record;


public final class DynamicEventApiDispatcher<T extends DynamicEventRestApi>
    extends AbstractDynamicContextDispatcher<T> {

    DynamicEventApiDispatcher(T api, String gatewayPath, ServiceDiscoveryController dispatcher) {
        super(api, gatewayPath, dispatcher);
    }

    @Override
    public Single<ResponseData> handle(HttpMethod httpMethod, String path, RoutingContext context) {
        if (get().useRequestData()) {
            return getDispatcher().executeEventMessageService(filter(httpMethod, path), path, httpMethod,
                                                              RequestDataConverter.convert(context));
        }
        return getDispatcher().executeEventMessageService(filter(httpMethod, path), path, httpMethod,
                                                          RequestDataConverter.body(context));
    }

    @Override
    public Predicate<Record> filter(HttpMethod method, String path) {
        return Functions.and(super.filter(method, path), record -> EventMethodDefinition.from(
            record.getMetadata().getJsonObject(EventMessageService.EVENT_METHOD_CONFIG)).search(path).isPresent());
    }

    @Override
    public void handleSuccess(RoutingContext context, ResponseData responseData) {
        EventMessage msg = EventMessage.tryParse(responseData.headers());
        if (msg.isError()) {
            handleError(context, JsonData.from(responseData.body(), ErrorMessage.class));
            return;
        }
        super.handleSuccess(context, responseData);
    }

}

package io.github.zero88.qwe.http.server.handler;

import java.util.function.Predicate;

import io.github.zero88.qwe.dto.ErrorMessage;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.dto.msg.ResponseData;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.http.server.converter.RequestDataConverter;
import io.github.zero88.qwe.http.server.handler.DynamicContextDispatcher.AbstractDynamicContextDispatcher;
import io.github.zero88.qwe.http.server.rest.DynamicEventRestApi;
import io.github.zero88.qwe.micro.ServiceDiscoveryInvoker;
import io.github.zero88.qwe.micro.http.EventMethodDefinition;
import io.github.zero88.qwe.micro.servicetype.EventMessageService;
import io.github.zero88.utils.Functions;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.servicediscovery.Record;

public final class DynamicEventApiDispatcher<T extends DynamicEventRestApi>
    extends AbstractDynamicContextDispatcher<T> {

    DynamicEventApiDispatcher(T api, String gatewayPath, ServiceDiscoveryInvoker dispatcher) {
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

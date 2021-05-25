package io.zero88.qwe.http.server.handler;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.http.server.converter.RequestDataConverter;
import io.zero88.qwe.http.server.handler.DynamicContextDispatcher.AbstractDynamicContextDispatcher;
import io.zero88.qwe.http.server.rest.api.DynamicHttpRestApi;
import io.zero88.qwe.micro.ServiceDiscoveryInvoker;

public final class DynamicHttpApiDispatcher<T extends DynamicHttpRestApi> extends AbstractDynamicContextDispatcher<T> {

    DynamicHttpApiDispatcher(T api, String gatewayPath, ServiceDiscoveryInvoker dispatcher) {
        super(api, gatewayPath, dispatcher);
    }

    @Override
    public Future<ResponseData> handle(HttpMethod httpMethod, String path, RoutingContext context) {
        return getDispatcher().executeHttpService(filter(httpMethod, path), path, httpMethod,
                                                  RequestDataConverter.convert(context));
    }

}

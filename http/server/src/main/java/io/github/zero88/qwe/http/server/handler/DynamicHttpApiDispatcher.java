package io.github.zero88.qwe.http.server.handler;

import io.github.zero88.qwe.dto.msg.ResponseData;
import io.github.zero88.qwe.http.server.converter.RequestDataConverter;
import io.github.zero88.qwe.http.server.handler.DynamicContextDispatcher.AbstractDynamicContextDispatcher;
import io.github.zero88.qwe.http.server.rest.api.DynamicHttpRestApi;
import io.github.zero88.qwe.micro.ServiceDiscoveryInvoker;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public final class DynamicHttpApiDispatcher<T extends DynamicHttpRestApi> extends AbstractDynamicContextDispatcher<T> {

    DynamicHttpApiDispatcher(T api, String gatewayPath, ServiceDiscoveryInvoker dispatcher) {
        super(api, gatewayPath, dispatcher);
    }

    @Override
    public Single<ResponseData> handle(HttpMethod httpMethod, String path, RoutingContext context) {
        return getDispatcher().executeHttpService(filter(httpMethod, path), path, httpMethod,
                                                  RequestDataConverter.convert(context));
    }

}

package io.github.zero88.msa.bp.http.server.handler;

import io.github.zero88.msa.bp.dto.msg.ResponseData;
import io.github.zero88.msa.bp.http.server.converter.RequestDataConverter;
import io.github.zero88.msa.bp.http.server.handler.DynamicContextDispatcher.AbstractDynamicContextDispatcher;
import io.github.zero88.msa.bp.http.server.rest.DynamicHttpRestApi;
import io.github.zero88.msa.bp.micro.ServiceDiscoveryController;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public final class DynamicHttpApiDispatcher<T extends DynamicHttpRestApi> extends AbstractDynamicContextDispatcher<T> {

    DynamicHttpApiDispatcher(T api, String gatewayPath, ServiceDiscoveryController dispatcher) {
        super(api, gatewayPath, dispatcher);
    }

    @Override
    public Single<ResponseData> handle(HttpMethod httpMethod, String path, RoutingContext context) {
        return getDispatcher().executeHttpService(filter(httpMethod, path), path, httpMethod,
                                                  RequestDataConverter.convert(context));
    }

}

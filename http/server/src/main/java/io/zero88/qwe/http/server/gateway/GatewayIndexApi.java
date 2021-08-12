package io.zero88.qwe.http.server.gateway;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.HttpServerPluginContext;
import io.zero88.qwe.http.server.rest.api.AbstractRestEventApi;
import io.zero88.qwe.micro.httpevent.ActionMethodMapping;

public final class GatewayIndexApi extends AbstractRestEventApi {

    @Override
    public GatewayIndexApi initRouter(SharedDataLocalProxy sharedData) {
        addRouter(sharedData.getData(HttpServerPluginContext.SERVER_GATEWAY_ADDRESS_DATA_KEY), "/index",
                  "/:identifier");
        return this;
    }

    @Override
    protected ActionMethodMapping initHttpEventMap() {
        return ActionMethodMapping.DQL_MAP;
    }

}

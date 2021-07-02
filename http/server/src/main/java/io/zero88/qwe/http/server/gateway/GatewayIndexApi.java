package io.zero88.qwe.http.server.gateway;

import io.zero88.qwe.http.ActionMethodMapping;
import io.zero88.qwe.http.server.HttpServerPlugin;
import io.zero88.qwe.http.server.rest.api.AbstractRestEventApi;

public final class GatewayIndexApi extends AbstractRestEventApi {

    @Override
    public GatewayIndexApi initRouter() {
        addRouter(this.proxy.getData(HttpServerPlugin.SERVER_GATEWAY_ADDRESS_DATA_KEY), "/index", "/:identifier");
        return this;
    }

    @Override
    protected ActionMethodMapping initHttpEventMap() {
        return ActionMethodMapping.DQL_MAP;
    }

}

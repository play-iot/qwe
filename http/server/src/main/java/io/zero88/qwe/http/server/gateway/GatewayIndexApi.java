package io.zero88.qwe.http.server.gateway;

import io.zero88.qwe.event.EventPattern;
import io.zero88.qwe.http.server.HttpServer;
import io.zero88.qwe.http.server.rest.api.AbstractRestEventApi;
import io.zero88.qwe.micro.http.ActionMethodMapping;
import io.zero88.qwe.micro.http.EventMethodDefinition;

public final class GatewayIndexApi extends AbstractRestEventApi {

    @Override
    public GatewayIndexApi initRouter() {
        addRouter(this.proxy.getData(HttpServer.SERVER_GATEWAY_ADDRESS_DATA_KEY), EventPattern.REQUEST_RESPONSE,
                  EventMethodDefinition.create("/index", "/:identifier", this));
        return this;
    }

    @Override
    protected ActionMethodMapping initHttpEventMap() {
        return ActionMethodMapping.DQL_MAP;
    }

}

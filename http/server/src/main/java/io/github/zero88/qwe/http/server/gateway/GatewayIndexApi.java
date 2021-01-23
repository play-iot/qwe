package io.github.zero88.qwe.http.server.gateway;

import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.http.server.HttpServer;
import io.github.zero88.qwe.http.server.rest.AbstractRestEventApi;
import io.github.zero88.qwe.micro.metadata.ActionMethodMapping;
import io.github.zero88.qwe.micro.metadata.EventMethodDefinition;

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

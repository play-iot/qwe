package io.github.zero88.msa.bp.http.server.gateway;

import io.github.zero88.msa.bp.event.EventPattern;
import io.github.zero88.msa.bp.http.event.ActionMethodMapping;
import io.github.zero88.msa.bp.http.event.EventMethodDefinition;
import io.github.zero88.msa.bp.http.server.HttpServer;
import io.github.zero88.msa.bp.http.server.rest.AbstractRestEventApi;

public final class GatewayIndexApi extends AbstractRestEventApi {

    @Override
    public GatewayIndexApi initRouter() {
        addRouter(getSharedDataValue(HttpServer.SERVER_GATEWAY_ADDRESS_DATA_KEY), EventPattern.REQUEST_RESPONSE,
                  EventMethodDefinition.create("/index", "/:identifier", this));
        return this;
    }

    @Override
    protected ActionMethodMapping initHttpEventMap() {
        return ActionMethodMapping.DQL_MAP;
    }

}

package io.zero88.qwe.http.server.gateway;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.ActionMethodMapping;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.server.config.ApiGatewayConfig;

import lombok.Getter;
import lombok.experimental.Accessors;

public final class GatewayIndexApi implements GatewayApi {

    @Getter
    @Accessors(fluent = true)
    private String address;
    @Getter
    private EventMethodDefinition definition;

    @Override
    public GatewayIndexApi setup(ApiGatewayConfig config, SharedDataLocalProxy sharedData) {
        this.address = config.getAddress();
        this.definition = EventMethodDefinition.create("/index", "/:identifier", mapping());
        return this;
    }

    @Override
    public ActionMethodMapping mapping() {
        return ActionMethodMapping.DQL_MAP;
    }

}

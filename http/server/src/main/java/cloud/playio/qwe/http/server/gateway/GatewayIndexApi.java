package cloud.playio.qwe.http.server.gateway;

import java.util.Collections;
import java.util.Set;

import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.http.ActionMethodMapping;
import cloud.playio.qwe.http.EventMethodDefinition;
import cloud.playio.qwe.http.server.config.ApiGatewayConfig;

import lombok.Getter;
import lombok.experimental.Accessors;

public final class GatewayIndexApi implements GatewayApi {

    @Getter
    @Accessors(fluent = true)
    private String address;

    @Override
    public GatewayIndexApi setup(ApiGatewayConfig config, SharedDataLocalProxy sharedData) {
        this.address = config.getAddress();
        return this;
    }

    @Override
    public Set<EventMethodDefinition> definitions() {
        return Collections.singleton(
            EventMethodDefinition.create("/index", "/:identifier", ActionMethodMapping.DQL_MAP));
    }

}

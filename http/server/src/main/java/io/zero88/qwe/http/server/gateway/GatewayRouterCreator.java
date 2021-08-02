package io.zero88.qwe.http.server.gateway;

import java.nio.file.Path;

import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.HttpServerPluginContext;
import io.zero88.qwe.http.server.config.ApiGatewayConfig;
import io.zero88.qwe.http.server.rest.ApisCreator.RestEventApisCreatorImpl;

import lombok.NonNull;

public class GatewayRouterCreator extends RestEventApisCreatorImpl<ApiGatewayConfig> {

    @Override
    public String function() {
        return "GatewayAPI";
    }

    @Override
    public @NonNull Router subRouter(@NonNull Path pluginDir, @NonNull ApiGatewayConfig config,
                                     @NonNull SharedDataLocalProxy sharedData) {
        sharedData.addData(HttpServerPluginContext.SERVER_GATEWAY_ADDRESS_DATA_KEY, config.getAddress());
        return super.subRouter(pluginDir, config, sharedData);
    }

    @Override
    protected String subFunction() {
        return "GatewayAPI";
    }

}

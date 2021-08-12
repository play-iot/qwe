package io.zero88.qwe.http.server.gateway;

import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Function;

import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.HttpRuntimeConfig;
import io.zero88.qwe.http.server.HttpServerConfig;
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
    public Function<HttpServerConfig, ApiGatewayConfig> lookupConfig() {
        return HttpServerConfig::getApiGatewayConfig;
    }

    @Override
    public @NonNull Router subRouter(@NonNull SharedDataLocalProxy sharedData, @NonNull Path pluginDir, @NonNull ApiGatewayConfig config) {
        sharedData.addData(HttpServerPluginContext.SERVER_GATEWAY_ADDRESS_DATA_KEY, config.getAddress());
        return super.subRouter(sharedData, pluginDir, config);
    }

    @Override
    protected String subFunction() {
        return "GatewayAPI";
    }

    @Override
    protected void register(HttpRuntimeConfig runtimeConfig) {
        register(Collections.singleton(runtimeConfig.getGatewayApiClass()));
    }

}

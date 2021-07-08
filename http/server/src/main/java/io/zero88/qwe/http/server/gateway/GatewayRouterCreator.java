package io.zero88.qwe.http.server.gateway;

import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.HttpServerPlugin;
import io.zero88.qwe.http.server.config.ApiGatewayConfig;
import io.zero88.qwe.http.server.rest.RestEventApisCreator;

import lombok.NonNull;

public class GatewayRouterCreator extends RestEventApisCreator<ApiGatewayConfig> {

    @Override
    public void doLogWhenRegister(ApiGatewayConfig config) {
        logger().info(config.decor("Register route [{}][{}]"), "Gateway APIs", config.getPath());
    }

    @Override
    public @NonNull Router router(@NonNull ApiGatewayConfig config, @NonNull SharedDataLocalProxy sharedData) {
        sharedData.addData(HttpServerPlugin.SERVER_GATEWAY_ADDRESS_DATA_KEY, config.getAddress());
        return super.router(config, sharedData);
    }

}

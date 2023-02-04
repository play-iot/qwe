package cloud.playio.qwe.http.server.gateway;

import java.util.function.Function;

import cloud.playio.qwe.http.server.HttpRuntimeConfig;
import cloud.playio.qwe.http.server.HttpServerConfig;
import cloud.playio.qwe.http.server.config.ApiGatewayConfig;
import cloud.playio.qwe.http.server.rest.RestEventApisCreatorImpl;

public final class GatewayRouterCreator extends RestEventApisCreatorImpl<GatewayApi, ApiGatewayConfig> {

    @Override
    public String function() {
        return "GatewayAPI";
    }

    @Override
    public Function<HttpServerConfig, ApiGatewayConfig> lookupConfig() {
        return HttpServerConfig::getApiGatewayConfig;
    }

    @Override
    protected String subFunction() {
        return "GatewayAPI";
    }

    @Override
    protected void register(HttpRuntimeConfig runtimeConfig) {
        register(runtimeConfig.getGatewayApiClasses());
    }

}

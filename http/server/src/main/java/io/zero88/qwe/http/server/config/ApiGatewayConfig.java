package io.zero88.qwe.http.server.config;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.RouterConfig;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public final class ApiGatewayConfig extends AbstractRouterConfig implements IConfig, RouterConfig {

    public static final String NAME = "__api_gateway__";

    private String address;

    public ApiGatewayConfig() {
        super(NAME, HttpServerConfig.class);
    }

    @Override
    protected @NonNull String defaultPath() {
        return BasePaths.ROOT_GATEWAY_PATH;
    }

}

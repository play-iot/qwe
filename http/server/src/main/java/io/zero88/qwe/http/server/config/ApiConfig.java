package io.zero88.qwe.http.server.config;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.RouterConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ApiConfig extends AbstractRouterConfig implements IConfig, RouterConfig, ApisSystem {

    public static final String NAME = "__api__";

    @JsonProperty(value = ApiDynamicRouteConfig.NAME)
    private ApiDynamicRouteConfig dynamicConfig = new ApiDynamicRouteConfig();

    public ApiConfig() {
        super(NAME, HttpServerConfig.class);
    }

    @Override
    protected String defaultPath() {
        return BasePaths.ROOT_API_PATH;
    }

}

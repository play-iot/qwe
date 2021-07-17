package io.zero88.qwe.http.server.config;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.http.server.RouterConfig;

public final class ApiDynamicRouteConfig extends AbstractRouterConfig implements IConfig, RouterConfig, ApisSystem {

    public static final String NAME = "__dynamic__";

    public ApiDynamicRouteConfig() {
        super(NAME, ApiConfig.class);
    }

    @Override
    protected String defaultPath() {
        return BasePaths.DYNAMIC_API_PATH;
    }

}

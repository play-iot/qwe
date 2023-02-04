package cloud.playio.qwe.http.server.config;

import cloud.playio.qwe.http.server.HttpSystem.ApisSystem;

public final class ApiProxyConfig extends AbstractRouterConfig implements ApisSystem {

    public static final String NAME = "__proxy__";

    public ApiProxyConfig() {
        super(NAME, ApiConfig.class);
    }

    @Override
    protected String defaultPath() {
        return "/s";
    }

}

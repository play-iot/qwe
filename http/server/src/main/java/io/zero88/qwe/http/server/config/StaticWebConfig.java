package io.zero88.qwe.http.server.config;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpConfig;
import io.zero88.qwe.http.server.RouterConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
public final class StaticWebConfig extends AbstractRouterConfig implements IConfig, RouterConfig {

    public static final String NAME = "__static__";
    private boolean inResource = true;
    private String webRoot = "webroot";

    public StaticWebConfig() {
        super(NAME, HttpConfig.class);
    }

    @Override
    protected @NonNull String defaultPath() {
        return BasePaths.WEB_PATH;
    }

}

package cloud.playio.qwe.http.server.config;

import cloud.playio.qwe.http.server.HttpServerConfig;
import cloud.playio.qwe.http.server.HttpSystem.WebSystem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
public final class StaticWebConfig extends AbstractRouterConfig implements WebSystem {

    public static final String NAME = "__static__";
    private boolean inResource = true;
    private String webRoot = "webroot";

    public StaticWebConfig() {
        super(NAME, HttpServerConfig.class);
    }

    @Override
    protected @NonNull String defaultPath() {
        return "/web";
    }

}

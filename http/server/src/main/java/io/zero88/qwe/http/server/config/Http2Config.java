package io.zero88.qwe.http.server.config;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.http.server.HttpServerConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor
public final class Http2Config implements IConfig {

    public static final String NAME = "__http2__";

    private boolean enabled = false;

    @Override
    public String configKey() { return NAME; }

    @Override
    public Class<? extends IConfig> parent() { return HttpServerConfig.class; }

}

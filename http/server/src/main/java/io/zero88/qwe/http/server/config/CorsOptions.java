package io.zero88.qwe.http.server.config;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import io.vertx.core.http.HttpMethod;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.HttpServerConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor
public final class CorsOptions implements IConfig {

    public static final String NAME = "__cors__";

    private String allowedOriginPattern = "*";
    private Set<String> allowedMethods = defaultAllowMethods();
    private Set<String> allowedHeaders = new HashSet<>();
    private Set<String> exposedHeaders = new HashSet<>();
    private boolean allowCredentials = false;
    private int maxAgeSeconds = 3600;

    @Override
    public String configKey() { return NAME; }

    @Override
    public Class<? extends IConfig> parent() { return HttpServerConfig.class; }

    public Set<HttpMethod> allowedMethods() {
        return this.allowedMethods.stream().map(HttpMethod::valueOf).collect(Collectors.toSet());
    }

    static Set<String> defaultAllowMethods() {
        return HttpUtils.DEFAULT_CORS_HTTP_METHOD.stream().map(HttpMethod::name).collect(Collectors.toSet());
    }

}

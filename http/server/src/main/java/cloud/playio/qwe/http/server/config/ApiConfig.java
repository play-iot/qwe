package cloud.playio.qwe.http.server.config;

import cloud.playio.qwe.http.server.HttpServerConfig;
import cloud.playio.qwe.http.server.HttpSystem.ApisSystem;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ApiConfig extends AbstractRouterConfig implements ApisSystem {

    public static final String NAME = "__api__";

    @JsonProperty(value = ApiProxyConfig.NAME)
    private ApiProxyConfig proxyConfig = new ApiProxyConfig();

    public ApiConfig() {
        super(NAME, HttpServerConfig.class);
        this.setEnabled(true);
    }

    @Override
    protected String defaultPath() {
        return "/api";
    }

}

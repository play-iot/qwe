package cloud.playio.qwe.http.server.config;

import cloud.playio.qwe.http.server.HttpServerConfig;
import cloud.playio.qwe.http.server.HttpSystem.GatewaySystem;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public final class ApiGatewayConfig extends AbstractRouterConfig implements GatewaySystem {

    public static final String NAME = "__api_gateway__";

    private String address;

    public ApiGatewayConfig() {
        super(NAME, HttpServerConfig.class);
    }

    @Override
    protected @NonNull String defaultPath() {
        return "/gw";
    }

}

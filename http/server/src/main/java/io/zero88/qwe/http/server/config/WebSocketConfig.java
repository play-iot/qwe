package io.zero88.qwe.http.server.config;

import java.util.Optional;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.RouterConfig;
import io.zero88.qwe.http.server.ws.WebSocketBridgeEventHandler;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
public final class WebSocketConfig extends AbstractRouterConfig implements IConfig, RouterConfig {

    public static final String NAME = "__websocket__";
    @Default
    private final String bridgeHandlerClass = WebSocketBridgeEventHandler.class.getName();
    @Default
    @JsonProperty(value = SockJSConfig.NAME)
    private final SockJSConfig sockjsOptions = new SockJSConfig();
    @Default
    @JsonProperty(value = SocketBridgeConfig.NAME)
    private final SocketBridgeConfig bridgeOptions = new SocketBridgeConfig();

    public WebSocketConfig() {
        super(NAME, HttpServerConfig.class, false, BasePaths.ROOT_WS_PATH);
    }

    public Class<? extends WebSocketBridgeEventHandler> bridgeHandlerClass() {
        return Optional.ofNullable(ReflectionClass.<WebSocketBridgeEventHandler>findClass(bridgeHandlerClass))
                       .orElse(WebSocketBridgeEventHandler.class);
    }

    @Override
    protected @NonNull String defaultPath() {
        return BasePaths.ROOT_WS_PATH;
    }

    public static class SockJSConfig extends SockJSHandlerOptions implements IConfig {

        public static final String NAME = "__sockjs__";

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return WebSocketConfig.class; }

    }


    public static class SocketBridgeConfig extends SockJSBridgeOptions implements IConfig {

        public static final String NAME = "__bridge__";

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return WebSocketConfig.class; }

    }

}

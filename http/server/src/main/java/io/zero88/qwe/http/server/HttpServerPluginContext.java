package io.zero88.qwe.http.server;

import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginContext.DefaultPluginContext;

import lombok.Getter;

@Getter
public final class HttpServerPluginContext extends DefaultPluginContext {

    public static final String SERVER_INFO_DATA_KEY = "SERVER_INFO";
    public static final String SERVER_GATEWAY_ADDRESS_DATA_KEY = "SERVER_GATEWAY_ADDRESS";
    private final ServerInfo serverInfo;

    protected HttpServerPluginContext(PluginContext context, ServerInfo serverInfo) {
        super(context);
        this.serverInfo = serverInfo;
    }

}

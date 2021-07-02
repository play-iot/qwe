package io.zero88.qwe.http.server;

import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginContext.DefaultPluginContext;

import lombok.Getter;

@Getter
public final class HttpServerPluginContext extends DefaultPluginContext {

    private final ServerInfo serverInfo;

    protected HttpServerPluginContext(PluginContext context, ServerInfo serverInfo) {
        super(context);
        this.serverInfo = serverInfo;
    }

}

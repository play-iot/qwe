package io.zero88.qwe.http.server;

import io.zero88.qwe.ComponentContext;
import io.zero88.qwe.ComponentContext.DefaultComponentContext;

import lombok.Getter;

@Getter
public final class HttpServerContext extends DefaultComponentContext {

    private final ServerInfo serverInfo;

    protected HttpServerContext(ComponentContext context, ServerInfo serverInfo) {
        super(context);
        this.serverInfo = serverInfo;
    }

}

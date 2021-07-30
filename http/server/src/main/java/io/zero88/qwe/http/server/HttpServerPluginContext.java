package io.zero88.qwe.http.server;

import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginContext.DefaultPluginContext;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public final class HttpServerPluginContext extends DefaultPluginContext {

    public static final String SERVER_INFO_DATA_KEY = "SERVER_INFO";
    public static final String SERVER_GATEWAY_ADDRESS_DATA_KEY = "SERVER_GATEWAY_ADDRESS";
    private ServerInfo serverInfo;

    HttpServerPluginContext(PluginContext context) {
        super(context);
    }

}

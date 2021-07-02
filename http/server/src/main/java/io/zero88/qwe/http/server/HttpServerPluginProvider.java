package io.zero88.qwe.http.server;

import io.zero88.qwe.PluginProvider;
import io.zero88.qwe.SharedDataLocalProxy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HttpServerPluginProvider implements PluginProvider<HttpServerPlugin> {

    private final HttpServerRouter httpRouter;

    @Override
    public HttpServerPlugin provide(SharedDataLocalProxy proxy) {
        return new HttpServerPlugin(proxy, httpRouter);
    }

    @Override
    public Class<HttpServerPlugin> pluginClass() {
        return HttpServerPlugin.class;
    }

}

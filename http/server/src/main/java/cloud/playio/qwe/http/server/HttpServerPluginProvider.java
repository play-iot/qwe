package cloud.playio.qwe.http.server;

import cloud.playio.qwe.PluginProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HttpServerPluginProvider implements PluginProvider<HttpServerPlugin> {

    private final HttpServerRouter httpRouter;

    @Override
    public HttpServerPlugin get() {
        return new HttpServerPlugin(httpRouter);
    }

    @Override
    public Class<HttpServerPlugin> pluginClass() {
        return HttpServerPlugin.class;
    }

}

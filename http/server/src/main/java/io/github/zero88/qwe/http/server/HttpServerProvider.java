package io.github.zero88.qwe.http.server;

import io.github.zero88.qwe.component.ComponentProvider;
import io.github.zero88.qwe.component.SharedDataLocalProxy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HttpServerProvider implements ComponentProvider<HttpServer> {

    private final HttpServerRouter httpRouter;

    @Override
    public HttpServer provide(SharedDataLocalProxy proxy) {
        return new HttpServer(proxy, httpRouter);
    }

    @Override
    public Class<HttpServer> componentClass() {
        return HttpServer.class;
    }

}

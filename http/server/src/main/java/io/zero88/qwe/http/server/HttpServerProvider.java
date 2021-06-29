package io.zero88.qwe.http.server;

import io.zero88.qwe.ComponentProvider;
import io.zero88.qwe.SharedDataLocalProxy;

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
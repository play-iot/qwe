package io.github.zero88.qwe.http.server;

import io.github.zero88.qwe.component.ComponentProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HttpServerProvider implements ComponentProvider<HttpServer> {

    private final HttpServerRouter httpRouter;

    @Override
    public Class<HttpServer> unitClass() { return HttpServer.class; }

    @Override
    public HttpServer get() { return new HttpServer(httpRouter); }

}

package io.github.zero88.msa.bp.http.server;

import io.github.zero88.msa.bp.component.UnitProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HttpServerProvider implements UnitProvider<HttpServer> {

    private final HttpServerRouter httpRouter;

    @Override
    public Class<HttpServer> unitClass() { return HttpServer.class; }

    @Override
    public HttpServer get() { return new HttpServer(httpRouter); }

}

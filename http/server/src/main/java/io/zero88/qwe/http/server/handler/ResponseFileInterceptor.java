package io.zero88.qwe.http.server.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.http.server.download.DownloadFile;

public final class ResponseFileInterceptor implements ResponseInterceptor<DownloadFile> {

    @Override
    public void response(RoutingContext ctx, DownloadFile file) {
        if (file.isFile()) {
            ctx.response().setChunked(true).setStatusCode(HttpResponseStatus.OK.code()).sendFile(file.getFilePath());
        } else {
            ctx.response().setChunked(true).setStatusCode(HttpResponseStatus.OK.code()).end(file.getContent());
        }
    }

}

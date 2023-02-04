package cloud.playio.qwe.http.server.handler;

import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import cloud.playio.qwe.dto.ErrorMessage;
import cloud.playio.qwe.http.HttpStatusMappingLoader;
import cloud.playio.qwe.http.HttpUtils;

public final class FailureContextHandler implements ResponseErrorHandler {

    @Override
    public void handle(RoutingContext ctx) {
        final Throwable failure = ctx.failure();
        if (failure == null) {
            ctx.next();
            return;
        }
        ErrorMessage msg = ErrorMessage.parse(failure);
        int c = failure instanceof HttpException
                ? ((HttpException) failure).getStatusCode()
                : HttpStatusMappingLoader.getInstance().get().error(ctx.request().method(), msg.getThrowable()).code();
        ctx.response()
           .putHeader(HttpHeaders.CONTENT_TYPE, HttpUtils.JSON_UTF8_CONTENT_TYPE)
           .setStatusCode(c)
           .end(HttpUtils.prettify(ctx.request(), msg));
    }

}

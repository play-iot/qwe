package io.zero88.qwe.http.server.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.http.HttpStatusMapping;
import io.zero88.qwe.http.HttpUtils;

public final class FailureContextHandler implements Handler<RoutingContext>, HasLogger {

    @Override
    public void handle(RoutingContext failureContext) {
        final HttpMethod method = failureContext.request().method();
        final Throwable failure = failureContext.failure();
        if (failure instanceof HttpException) {
            final int statusCode = ((HttpException) failure).getStatusCode();
            failureContext.response()
                          .putHeader(HttpHeaders.CONTENT_TYPE, HttpUtils.JSON_UTF8_CONTENT_TYPE)
                          .setStatusCode(statusCode)
                          .end(HttpUtils.prettify(failureContext.request(), ErrorMessage.parse(failure)));
        } else if (failure != null) {
            logger().error("API exception", failure);
            final ErrorMessage errorMessage = ErrorMessage.parse(failure);
            failureContext.response()
                          .putHeader(HttpHeaders.CONTENT_TYPE, HttpUtils.JSON_UTF8_CONTENT_TYPE)
                          .setStatusCode(HttpStatusMapping.error(method, errorMessage.getThrowable()).code())
                          .end(HttpUtils.prettify(failureContext.request(), errorMessage));
        }
    }

}

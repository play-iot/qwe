package io.github.zero88.qwe.http.server.handler;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.dto.ErrorMessage;
import io.github.zero88.qwe.http.HttpStatusMapping;
import io.github.zero88.qwe.http.HttpUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public final class FailureContextHandler implements Handler<RoutingContext> {

    private final Logger logger = LoggerFactory.getLogger(FailureContextHandler.class);

    @Override
    public void handle(RoutingContext failureContext) {
        final HttpMethod method = failureContext.request().method();
        final Throwable throwable = failureContext.failure();
        if (Objects.nonNull(throwable)) {
            logger.error("API exception", throwable);
            ErrorMessage errorMessage = ErrorMessage.parse(throwable);
            failureContext.response()
                          .putHeader(HttpHeaders.CONTENT_TYPE, HttpUtils.JSON_UTF8_CONTENT_TYPE)
                          .setStatusCode(HttpStatusMapping.error(method, errorMessage.getThrowable()).code())
                          .end(HttpUtils.prettify(errorMessage, failureContext.request()));
        }
    }

}

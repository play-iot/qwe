package io.github.zero88.msa.bp.http.server.handler;

import java.util.Objects;

import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.http.HttpStatusMapping;
import io.github.zero88.msa.bp.http.HttpUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

/**
 * Rest response end handler for {@code eventbus}
 */
public final class RestEventResponseHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext context) {
        context.addHeadersEndHandler(
            v -> context.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpUtils.JSON_UTF8_CONTENT_TYPE));
        HttpMethod method = context.request().method();
        EventMessage eventMessage = context.get(EventAction.RETURN.type());
        if (Objects.isNull(eventMessage)) {
            context.next();
            return;
        }
        if (eventMessage.isSuccess()) {
            context.response()
                   .setStatusCode(HttpStatusMapping.success(method).code())
                   .end(HttpUtils.prettify(eventMessage.getData(), context.request()));
        } else {
            context.response()
                   .setStatusCode(HttpStatusMapping.error(method, eventMessage.getError().getCode()).code())
                   .end(HttpUtils.prettify(eventMessage.getError().toJson(), context.request()));
        }
    }

}

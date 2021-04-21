package io.zero88.qwe.http.server.handler;

import java.util.Objects;

import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.HttpStatusMapping;
import io.zero88.qwe.http.HttpUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

/**
 * Rest response end handler for {@code eventbus}
 *
 * @see EventMessage
 */
public final class EventMessageResponseHandler implements Handler<RoutingContext> {

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

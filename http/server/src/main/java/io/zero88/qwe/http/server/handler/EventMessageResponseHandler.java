package io.zero88.qwe.http.server.handler;

import java.util.Objects;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.eventbus.EventMessage;
import io.zero88.qwe.http.HttpStatusMappingLoader;
import io.zero88.qwe.http.HttpUtils;

/**
 * Rest response end handler for {@code eventbus}
 *
 * @see EventMessage
 */
public final class EventMessageResponseHandler implements ResponseWriter {

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
                   .setStatusCode(HttpStatusMappingLoader.getInstance().get().success(method).code())
                   .end(HttpUtils.prettify(context.request(), eventMessage.getData()));
        } else {
            context.response()
                   .setStatusCode(HttpStatusMappingLoader.getInstance()
                                                         .get()
                                                         .error(method, eventMessage.getError().getCode())
                                                         .code())
                   .end(HttpUtils.prettify(context.request(), eventMessage.getError()));
        }
    }

}

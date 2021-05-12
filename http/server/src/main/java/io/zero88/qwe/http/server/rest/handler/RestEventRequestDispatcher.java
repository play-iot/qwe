package io.zero88.qwe.http.server.rest.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.EventPattern;

public interface RestEventRequestDispatcher extends Handler<RoutingContext> {

    EventBusClient eventbus();

    default void dispatch(RoutingContext context, String system, String address, EventPattern pattern,
                          EventMessage message) {
        eventbus().request(address, message)
                  .onSuccess(msg -> context.put(EventAction.RETURN.type(), msg).next())
                  .onFailure(context::fail);
    }

}

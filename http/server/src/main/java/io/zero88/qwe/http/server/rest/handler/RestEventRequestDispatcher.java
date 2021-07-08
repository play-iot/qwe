package io.zero88.qwe.http.server.rest.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusProxy;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.EventPattern;

public interface RestEventRequestDispatcher extends Handler<RoutingContext>, EventBusProxy, HasLogger {

    default void dispatch(RoutingContext context, String address, EventPattern pattern, EventMessage message) {
        transporter().fire(address, pattern, message)
                     .onSuccess(msg -> context.put(EventAction.RETURN.type(), msg).next())
                     .onFailure(context::fail);
    }

}

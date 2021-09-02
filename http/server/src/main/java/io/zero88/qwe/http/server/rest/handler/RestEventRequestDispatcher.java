package io.zero88.qwe.http.server.rest.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.eventbus.EventBusProxy;
import io.zero88.qwe.eventbus.EventMessage;
import io.zero88.qwe.eventbus.EventPattern;
import io.zero88.qwe.http.server.HttpSystem.ApisSystem;

public interface RestEventRequestDispatcher extends Handler<RoutingContext>, EventBusProxy, HasLogger, ApisSystem {

    default void dispatch(RoutingContext context, String address, EventPattern pattern, EventMessage message) {
        logger().info(decor("Dispatch request to Event[{}]"), address);
        transporter().fire(address, pattern, message)
                     .onSuccess(msg -> context.put(EventAction.RETURN.type(), msg).next())
                     .onFailure(context::fail);
    }

}

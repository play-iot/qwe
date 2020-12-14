package io.github.zero88.msa.bp.http.server.handler;

import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.event.EventPattern;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.event.ReplyEventHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface RestEventRequestDispatcher extends Handler<RoutingContext> {

    EventbusClient getController();

    default void dispatch(RoutingContext context, String system, String address, EventPattern pattern,
                          EventMessage message) {
        ReplyEventHandler handler = ReplyEventHandler.builder()
                                                     .system(system).address(address)
                                                     .action(message.getAction())
                                                     .success(respMsg -> {
                                                         context.put(EventAction.RETURN.type(), respMsg);
                                                         context.next();
                                                     })
                                                     .exception(context::fail)
                                                     .build();
        getController().fire(address, pattern, message, handler);
    }

}

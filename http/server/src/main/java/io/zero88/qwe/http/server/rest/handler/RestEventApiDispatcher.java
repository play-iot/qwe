package io.zero88.qwe.http.server.rest.handler;

import java.util.Objects;

import io.github.zero88.repl.Arguments;
import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.EventPattern;
import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.http.server.converter.RequestDataConverter;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents for pushing data via {@code EventBus} then listen {@code reply message}. After receiving {@code reply
 * message}, redirect it to {@code next Context handler}
 *
 * @see EventMessageResponseHandler
 */
@RequiredArgsConstructor
public class RestEventApiDispatcher implements RestEventRequestDispatcher {

    @Getter
    @NonNull
    @Accessors(fluent = true)
    private final EventBusClient transporter;
    @NonNull
    private final String address;
    @NonNull
    private final EventAction action;
    @NonNull
    private final EventPattern pattern;
    private final boolean useRequestData;

    @SuppressWarnings("unchecked")
    public static <T extends RestEventApiDispatcher> RestEventApiDispatcher create(Class<T> handler,
                                                                                   EventBusClient eventbus,
                                                                                   String address, EventAction action,
                                                                                   EventPattern pattern,
                                                                                   boolean useRequestData) {
        Class<T> handlerClass = Objects.isNull(handler) ? (Class<T>) RestEventApiDispatcher.class : handler;
        return ReflectionClass.createObject(handlerClass, new Arguments().put(EventBusClient.class, eventbus)
                                                                         .put(String.class,
                                                                              Strings.requireNotBlank(address))
                                                                         .put(EventAction.class, action)
                                                                         .put(EventPattern.class, pattern)
                                                                         .put(boolean.class, useRequestData));
    }

    @Override
    public void handle(RoutingContext context) {
        EventMessage msg = useRequestData
                           ? EventMessage.initial(action, RequestDataConverter.convert(context))
                           : EventMessage.initial(action, RequestDataConverter.body(context));
        dispatch(context, address, pattern, msg);
    }

}

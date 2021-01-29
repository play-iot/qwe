package io.github.zero88.qwe.http.server.rest.handler;

import java.util.LinkedHashMap;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.http.server.converter.RequestDataConverter;
import io.github.zero88.qwe.http.server.handler.EventMessageResponseHandler;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.ext.web.RoutingContext;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents for pushing data via {@code Eventbus} then listen {@code reply message}. After receiving {@code reply
 * message}, redirect it to {@code next Context handler}
 *
 * @see EventMessageResponseHandler
 */
@RequiredArgsConstructor
public class RestEventApiDispatcher implements RestEventRequestDispatcher {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Getter
    @NonNull
    @Accessors(fluent = true)
    private final EventbusClient eventbus;
    @NonNull
    private final String address;
    @NonNull
    private final EventAction action;
    @NonNull
    private final EventPattern pattern;
    private final boolean useRequestData;

    @SuppressWarnings("unchecked")
    public static <T extends RestEventApiDispatcher> RestEventApiDispatcher create(Class<T> handler,
                                                                                   EventbusClient eventbus,
                                                                                   String address, EventAction action,
                                                                                   EventPattern pattern,
                                                                                   boolean useRequestData) {
        Class<T> handlerClass = Objects.isNull(handler) ? (Class<T>) RestEventApiDispatcher.class : handler;
        LinkedHashMap<Class, Object> inputs = new LinkedHashMap<>();
        inputs.put(EventbusClient.class, eventbus);
        inputs.put(String.class, Strings.requireNotBlank(address));
        inputs.put(EventAction.class, action);
        inputs.put(EventPattern.class, pattern);
        inputs.put(boolean.class, useRequestData);
        return ReflectionClass.createObject(handlerClass, inputs);
    }

    @Override
    public void handle(RoutingContext context) {
        EventMessage msg = useRequestData
                           ? EventMessage.initial(action, RequestDataConverter.convert(context))
                           : EventMessage.initial(action, RequestDataConverter.body(context));
        logger.info("REST::Dispatch data to Event Address {}", address);
        dispatch(context, "REST", address, pattern, msg);
    }

}

package io.zero88.qwe.http.client.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.Handler;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventModel;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class WebSocketResponseErrorHandler implements Handler<Throwable> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @NonNull
    private final EventBusClient eventbus;
    @NonNull
    private final EventModel listener;

    @SuppressWarnings("unchecked")
    public static <T extends WebSocketResponseErrorHandler> T create(@NonNull EventBusClient controller,
                                                                     @NonNull EventModel listener,
                                                                     @NonNull Class<T> errorHandlerClass) {
        if (Objects.isNull(errorHandlerClass) || WebSocketResponseErrorHandler.class.equals(errorHandlerClass)) {
            return (T) new IgnoreWebSocketResponseError(controller, listener) {};
        }
        Map<Class, Object> params = new LinkedHashMap<>();
        params.put(EventBusClient.class, controller);
        params.put(EventModel.class, listener);
        return ReflectionClass.createObject(errorHandlerClass, params);
    }

    public static class IgnoreWebSocketResponseError extends WebSocketResponseErrorHandler {

        IgnoreWebSocketResponseError(@NonNull EventBusClient controller, @NonNull EventModel listener) {
            super(controller, listener);
        }

        @Override
        public void handle(Throwable error) {
            this.logger.warn("Error in websocket response", error);
        }

    }

}

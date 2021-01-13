package io.github.zero88.msa.bp.http.client.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class WebSocketResponseErrorHandler implements Handler<Throwable> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @NonNull
    private final EventbusClient controller;
    @NonNull
    private final EventModel listener;

    @SuppressWarnings("unchecked")
    public static <T extends WebSocketResponseErrorHandler> T create(@NonNull EventbusClient controller,
                                                                     @NonNull EventModel listener,
                                                                     @NonNull Class<T> errorHandlerClass) {
        if (Objects.isNull(errorHandlerClass) || WebSocketResponseErrorHandler.class.equals(errorHandlerClass)) {
            return (T) new IgnoreWebSocketResponseError(controller, listener) {};
        }
        Map<Class, Object> params = new LinkedHashMap<>();
        params.put(EventbusClient.class, controller);
        params.put(EventModel.class, listener);
        return ReflectionClass.createObject(errorHandlerClass, params);
    }

    public static class IgnoreWebSocketResponseError extends WebSocketResponseErrorHandler {

        IgnoreWebSocketResponseError(@NonNull EventbusClient controller, @NonNull EventModel listener) {
            super(controller, listener);
        }

        @Override
        public void handle(Throwable error) {
            this.logger.warn("Error in websocket response", error);
        }

    }

}

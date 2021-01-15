package io.github.zero88.msa.bp.http.client.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.http.event.WebSocketServerEventMetadata;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Handle light Websocket response then dispatch based on Event Listener
 *
 * @see WebSocketServerEventMetadata
 */
@RequiredArgsConstructor
public abstract class WebSocketResponseDispatcher implements Handler<Buffer> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NonNull
    private final EventbusClient eventbus;
    @NonNull
    private final EventModel listener;

    @SuppressWarnings("unchecked")
    public static <T extends WebSocketResponseDispatcher> T create(@NonNull EventbusClient controller,
                                                                   @NonNull EventModel listener,
                                                                   @NonNull Class<T> bodyHandlerClass) {
        if (Objects.isNull(bodyHandlerClass) || WebSocketResponseDispatcher.class.equals(bodyHandlerClass)) {
            return (T) new WebSocketResponseDispatcher(controller, listener) {};
        }
        Map<Class, Object> params = new LinkedHashMap<>();
        params.put(EventbusClient.class, controller);
        params.put(EventModel.class, listener);
        return ReflectionClass.createObject(bodyHandlerClass, params);
    }

    @Override
    public void handle(Buffer data) {
        logger.info("Websocket Client received message then dispatch data to '{}'", listener.getAddress());
        eventbus.fire(listener.getAddress(), listener.getPattern(),
                      EventMessage.tryParse(JsonData.tryParse(data), true));
    }

}

package io.zero88.qwe.http.client.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.EventModel;
import io.zero88.qwe.http.event.WebSocketServerEventMetadata;

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
    private final EventBusClient eventbus;
    @NonNull
    private final EventModel listener;

    @SuppressWarnings("unchecked")
    public static <T extends WebSocketResponseDispatcher> T create(@NonNull EventBusClient client,
                                                                   @NonNull EventModel listener,
                                                                   Class<T> bodyHandlerClass) {
        if (Objects.isNull(bodyHandlerClass) || WebSocketResponseDispatcher.class.equals(bodyHandlerClass)) {
            return (T) new WebSocketResponseDispatcher(client, listener) {};
        }
        Map<Class, Object> params = new LinkedHashMap<>();
        params.put(EventBusClient.class, client);
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

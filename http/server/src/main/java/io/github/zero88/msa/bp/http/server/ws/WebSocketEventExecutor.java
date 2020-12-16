package io.github.zero88.msa.bp.http.server.ws;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.zero88.msa.bp.dto.msg.RequestData;
import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.event.EventPattern;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.event.ReplyEventHandler;
import io.github.zero88.msa.bp.http.event.WebSocketServerEventMetadata;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebSocketEventExecutor {

    private static final String WEBSOCKET_SERVER = "WEBSOCKET_SERVER";
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventExecutor.class);
    private final EventbusClient controller;

    public void execute(@NonNull WebSocketEventMessage socketMessage, @NonNull WebSocketServerEventMetadata metadata,
                        @NonNull Consumer<EventMessage> callback) {
        EventMessage msg = EventMessage.success(socketMessage.getBody().getAction(),
                                                RequestData.from(socketMessage.getBody()));
        logger.info("WEBSOCKET::Client Request: {}", msg.toJson().encode());
        EventModel processor = metadata.getProcessor();
        if (processor.getPattern() == EventPattern.REQUEST_RESPONSE) {
            ReplyEventHandler handler = ReplyEventHandler.builder()
                                                         .system(WEBSOCKET_SERVER)
                                                         .address(processor.getAddress())
                                                         .action(msg.getAction())
                                                         .success(callback(metadata.getPublisher(), callback))
                                                         .build();
            controller.fire(processor.getAddress(), processor.getPattern(), msg, handler);
        } else {
            controller.fire(processor.getAddress(), processor.getPattern(), msg);
            callback.accept(EventMessage.success(EventAction.RETURN));
        }
    }

    private Consumer<EventMessage> callback(EventModel publisher, Consumer<EventMessage> defCallback) {
        if (Objects.isNull(publisher)) {
            return defCallback;
        }
        return eventMessage -> controller.fire(publisher.getAddress(), publisher.getPattern(), eventMessage);
    }

}

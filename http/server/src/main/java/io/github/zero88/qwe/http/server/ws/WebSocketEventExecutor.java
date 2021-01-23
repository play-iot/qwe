package io.github.zero88.qwe.http.server.ws;

import java.util.Objects;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.dto.msg.RequestData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.event.ReplyEventHandler;
import io.github.zero88.qwe.http.event.WebSocketServerEventMetadata;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebSocketEventExecutor {

    private static final String WEBSOCKET_SERVER = "WEBSOCKET_SERVER";
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventExecutor.class);
    private final EventbusClient eventbus;

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
            eventbus.fire(processor.getAddress(), processor.getPattern(), msg, handler);
        } else {
            eventbus.fire(processor.getAddress(), processor.getPattern(), msg);
            callback.accept(EventMessage.success(EventAction.RETURN));
        }
    }

    private Consumer<EventMessage> callback(EventModel publisher, Consumer<EventMessage> defCallback) {
        if (Objects.isNull(publisher)) {
            return defCallback;
        }
        return eventMessage -> eventbus.fire(publisher.getAddress(), publisher.getPattern(), eventMessage);
    }

}

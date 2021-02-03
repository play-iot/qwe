package io.github.zero88.qwe.http.server.ws;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.zero88.qwe.component.HasSharedData;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.dto.msg.RequestData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.event.ReplyEventHandler;
import io.github.zero88.qwe.http.event.WebSocketServerEventMetadata;
import io.github.zero88.qwe.http.server.HttpLogSystem.WebSocketLogSystem;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class WebSocketEventExecutor implements HasSharedData, WebSocketLogSystem {

    private final SharedDataLocalProxy sharedData;

    public void execute(@NonNull WebSocketEventMessage socketMessage, @NonNull WebSocketServerEventMetadata metadata,
                        @NonNull Consumer<EventMessage> callback) {
        final EventMessage msg = EventMessage.success(socketMessage.getBody().getAction(),
                                                      RequestData.from(socketMessage.getBody()));
        final EventbusClient eventbus = EventbusClient.create(sharedData);
        log.info(decor("Handle action '{}' from client"), msg.getAction());
        EventModel processor = metadata.getProcessor();
        if (processor.getPattern() == EventPattern.REQUEST_RESPONSE) {
            ReplyEventHandler handler = ReplyEventHandler.builder()
                                                         .system(function())
                                                         .address(processor.getAddress())
                                                         .action(msg.getAction())
                                                         .success(callback(eventbus, metadata.getPublisher(), callback))
                                                         .build();
            eventbus.fire(processor.getAddress(), processor.getPattern(), msg, handler);
        } else {
            eventbus.fire(processor.getAddress(), processor.getPattern(), msg);
            callback.accept(EventMessage.success(EventAction.RETURN));
        }
    }

    private Consumer<EventMessage> callback(EventbusClient eventbus, EventModel publisher,
                                            Consumer<EventMessage> defCallback) {
        if (Objects.isNull(publisher)) {
            return defCallback;
        }
        return eventMessage -> eventbus.fire(publisher.getAddress(), publisher.getPattern(), eventMessage);
    }

}

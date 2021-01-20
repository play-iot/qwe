package io.github.zero88.qwe.http.server.mock;

import java.util.Arrays;
import java.util.List;

import io.github.zero88.qwe.dto.msg.RequestData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventContractor;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.http.event.WebSocketServerEventMetadata;
import io.vertx.reactivex.core.eventbus.EventBus;

public class MockWebSocketEvent {

    public static final EventModel SERVER_PROCESSOR = EventModel.builder()
                                                                .address("server.processor")
                                                                .pattern(EventPattern.REQUEST_RESPONSE)
                                                                .events(Arrays.asList(EventAction.GET_LIST,
                                                                                      EventAction.GET_ONE))
                                                                .build();
    public static final EventModel SERVER_LISTENER = EventModel.clone(SERVER_PROCESSOR, "socket.client2server",
                                                                      EventPattern.REQUEST_RESPONSE);
    public static final EventModel SERVER_PUBLISHER = EventModel.builder()
                                                                .address("socket.server2client")
                                                                .pattern(EventPattern.PUBLISH_SUBSCRIBE)
                                                                .event(EventAction.RETURN)
                                                                .build();
    public static final WebSocketServerEventMetadata ALL_EVENTS = WebSocketServerEventMetadata.create(SERVER_LISTENER,
                                                                                                      SERVER_PROCESSOR,
                                                                                                      SERVER_PUBLISHER);
    public static final WebSocketServerEventMetadata NO_PUBLISHER = WebSocketServerEventMetadata.create(SERVER_LISTENER,
                                                                                                        SERVER_PROCESSOR);
    public static final WebSocketServerEventMetadata ONLY_PUBLISHER = WebSocketServerEventMetadata.create("rtc",
                                                                                                          SERVER_PUBLISHER);


    public static class MockWebSocketEventServerListener extends MockEventBusListener {

        public MockWebSocketEventServerListener(EventBus eventBus) {
            super(eventBus, SERVER_PROCESSOR);
        }

        @EventContractor(action = "GET_LIST", returnType = List.class)
        public List<String> list(RequestData data) {
            return Arrays.asList("1", "2", "3");
        }

        @EventContractor(action = "GET_ONE", returnType = String.class)
        public String one(RequestData data) {
            return "1";
        }

    }

}

package io.zero88.qwe.http.server.mock;

import java.util.Arrays;
import java.util.List;

import io.vertx.core.eventbus.EventBus;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.http.event.EventModel;
import io.zero88.qwe.event.EventPattern;
import io.zero88.qwe.http.event.WebSocketServerEventMetadata;

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

        public MockWebSocketEventServerListener() {
//            super(SERVER_PROCESSOR);
        }

        @EBContract(action = "GET_LIST")
        public List<String> list(RequestData data) {
            return Arrays.asList("1", "2", "3");
        }

        @EBContract(action = "GET_ONE")
        public String one(RequestData data) {
            return "1";
        }

    }

}

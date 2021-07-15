package io.zero88.qwe.http.server.ws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.zero88.qwe.http.server.mock.MockWebSocketEvent;

public class WebSocketServerPlanTest {

    @Test
    public void test_no_addresses() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> WebSocketServerPlan.createOutbound(null));
    }

    @Test
    public void test_register_with_no_path_no_publisher() {
        WebSocketServerPlan metadata = WebSocketServerPlan.createInbound(MockWebSocketEvent.INBOUND,
                                                                         MockWebSocketEvent.PROCESSOR);
        Assertions.assertEquals("/", metadata.getPath());
        Assertions.assertEquals(MockWebSocketEvent.INBOUND, metadata.inbound());
        Assertions.assertEquals(MockWebSocketEvent.PROCESSOR, metadata.processor());
        Assertions.assertNull(metadata.outbound());
    }

    @Test
    public void test_register_with_path_and_full_event() {
        WebSocketServerPlan metadata = WebSocketServerPlan.createInbound("xy", MockWebSocketEvent.INBOUND,
                                                                         MockWebSocketEvent.PROCESSOR,
                                                                         MockWebSocketEvent.OUTBOUND);
        Assertions.assertEquals("/xy", metadata.getPath());
        Assertions.assertEquals(MockWebSocketEvent.INBOUND, metadata.inbound());
        Assertions.assertEquals(MockWebSocketEvent.PROCESSOR, metadata.processor());
        Assertions.assertEquals(MockWebSocketEvent.OUTBOUND, metadata.outbound());
    }

    @Test
    public void test_register_with_path_and_no_publisher() {
        WebSocketServerPlan metadata = WebSocketServerPlan.createOutbound("ab", MockWebSocketEvent.OUTBOUND);
        Assertions.assertEquals("/ab", metadata.getPath());
        Assertions.assertEquals(MockWebSocketEvent.OUTBOUND, metadata.outbound());
        Assertions.assertNull(metadata.inbound());
        Assertions.assertNull(metadata.processor());
    }

}

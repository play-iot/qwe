package io.github.zero88.msa.bp.http.server.ws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.event.EventPattern;
import io.github.zero88.msa.bp.exceptions.InitializerError;
import io.github.zero88.msa.bp.http.event.WebSocketServerEventMetadata;
import io.github.zero88.msa.bp.http.server.mock.MockWebSocketEvent;

public class WebSocketServerEventMetadataTest {

    @Test
    public void test_no_addresses() {
        Assertions.assertThrows(InitializerError.class, () -> WebSocketServerEventMetadata.create("xy", null));
    }

    @Test
    public void test_register_with_no_path_no_publisher() {
        WebSocketServerEventMetadata metadata = WebSocketServerEventMetadata.create(MockWebSocketEvent.SERVER_LISTENER,
                                                                                    MockWebSocketEvent.SERVER_PROCESSOR);
        Assertions.assertEquals("/", metadata.getPath());
        Assertions.assertEquals(MockWebSocketEvent.SERVER_LISTENER, metadata.getListener());
        Assertions.assertEquals(MockWebSocketEvent.SERVER_PROCESSOR, metadata.getProcessor());
        Assertions.assertNull(metadata.getPublisher());
    }

    @Test
    public void test_register_with_path_and_full_event() {
        WebSocketServerEventMetadata metadata = WebSocketServerEventMetadata.create("xy",
                                                                                    MockWebSocketEvent.SERVER_LISTENER,
                                                                                    MockWebSocketEvent.SERVER_PROCESSOR,
                                                                                    MockWebSocketEvent.SERVER_PUBLISHER);
        Assertions.assertEquals("/xy", metadata.getPath());
        Assertions.assertEquals(MockWebSocketEvent.SERVER_LISTENER, metadata.getListener());
        Assertions.assertEquals(MockWebSocketEvent.SERVER_PROCESSOR, metadata.getProcessor());
        Assertions.assertEquals(MockWebSocketEvent.SERVER_PUBLISHER, metadata.getPublisher());
    }

    @Test
    public void test_register_with_path_and_no_publisher() {
        WebSocketServerEventMetadata metadata = WebSocketServerEventMetadata.create("ab",
                                                                                    MockWebSocketEvent.SERVER_PUBLISHER);
        Assertions.assertEquals("/ab", metadata.getPath());
        Assertions.assertEquals(MockWebSocketEvent.SERVER_PUBLISHER, metadata.getPublisher());
        Assertions.assertNull(metadata.getListener());
        Assertions.assertNull(metadata.getProcessor());
    }

    @Test
    public void test_register_listener_invalid_pattern() {
        Assertions.assertThrows(InitializerError.class, () -> WebSocketServerEventMetadata.create(
            EventModel.clone(MockWebSocketEvent.SERVER_LISTENER, "invalid", EventPattern.PUBLISH_SUBSCRIBE),
            MockWebSocketEvent.SERVER_PROCESSOR));
    }

}

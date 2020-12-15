package io.github.zero88.msa.bp.http.server.ws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.msa.bp.exceptions.InitializerError;
import io.github.zero88.msa.bp.http.event.WebSocketServerEventMetadata;
import io.github.zero88.msa.bp.http.server.mock.MockWebSocketEvent;

public class WebSocketEventBuilderTest {

    @Test
    public void test_no_register_metadata() {
        Assertions.assertThrows(InitializerError.class, () -> new WebSocketEventBuilder().validate());
    }

    @Test
    public void test_register_null() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> new WebSocketEventBuilder().register((WebSocketServerEventMetadata) null));
    }

    @Test
    public void test_customize_root() {
        WebSocketEventBuilder builder = new WebSocketEventBuilder();
        Assertions.assertEquals("/ws", builder.getRootWs());
        builder.rootWs("rtc");
        Assertions.assertEquals("/rtc", builder.getRootWs());
    }

    @Test
    public void test_one_metadata() {
        WebSocketServerEventMetadata metadata = WebSocketServerEventMetadata.create(MockWebSocketEvent.SERVER_LISTENER,
                                                                                    MockWebSocketEvent.SERVER_PROCESSOR);
        Assertions.assertEquals(1, new WebSocketEventBuilder().register(metadata).validate().size());
    }

    @Test
    public void test_register_many_metadata_with_same_path() {
        WebSocketServerEventMetadata metadata1 = WebSocketServerEventMetadata.create("xy",
                                                                                     MockWebSocketEvent.SERVER_LISTENER,
                                                                                     MockWebSocketEvent.SERVER_PROCESSOR);
        WebSocketServerEventMetadata metadata2 = WebSocketServerEventMetadata.create("xy",
                                                                                     MockWebSocketEvent.SERVER_LISTENER,
                                                                                     MockWebSocketEvent.SERVER_PROCESSOR);
        Assertions.assertEquals(1, new WebSocketEventBuilder().register(metadata1, metadata2).validate().size());
    }

    @Test
    public void test_register_many_metadata_with_different_path() {
        WebSocketServerEventMetadata metadata1 = WebSocketServerEventMetadata.create("xy",
                                                                                     MockWebSocketEvent.SERVER_LISTENER,
                                                                                     MockWebSocketEvent.SERVER_PROCESSOR);
        WebSocketServerEventMetadata metadata2 = WebSocketServerEventMetadata.create("abc",
                                                                                     MockWebSocketEvent.SERVER_LISTENER,
                                                                                     MockWebSocketEvent.SERVER_PROCESSOR);
        Assertions.assertEquals(2, new WebSocketEventBuilder().register(metadata1, metadata2).validate().size());
    }

}

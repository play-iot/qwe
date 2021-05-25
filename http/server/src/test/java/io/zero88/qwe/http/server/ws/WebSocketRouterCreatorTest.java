package io.zero88.qwe.http.server.ws;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.http.event.WebSocketServerEventMetadata;
import io.zero88.qwe.http.server.HttpConfig.WebSocketConfig;
import io.zero88.qwe.http.server.mock.MockWebSocketEvent;

public class WebSocketRouterCreatorTest {

    @Test
    public void test_no_register_metadata() {
        Assertions.assertThrows(InitializerError.class,
                                () -> new WebSocketRouterCreator(Collections.emptyList()).validate());
    }

    @Test
    public void test_customize_root() {
        final WebSocketRouterCreator creator = new WebSocketRouterCreator(Collections.emptySet());
        final String s = creator.mountPoint(WebSocketConfig.builder().build());
        Assertions.assertEquals("/ws", s);
    }

    @Test
    public void test_one_metadata() {
        WebSocketServerEventMetadata metadata = WebSocketServerEventMetadata.create(MockWebSocketEvent.SERVER_LISTENER,
                                                                                    MockWebSocketEvent.SERVER_PROCESSOR);
        Assertions.assertEquals(1, new WebSocketRouterCreator(Collections.singletonList(metadata)).validate().size());
    }

    @Test
    public void test_register_many_metadata_with_same_path() {
        WebSocketServerEventMetadata metadata1 = WebSocketServerEventMetadata.create("xy",
                                                                                     MockWebSocketEvent.SERVER_LISTENER,
                                                                                     MockWebSocketEvent.SERVER_PROCESSOR);
        WebSocketServerEventMetadata metadata2 = WebSocketServerEventMetadata.create("xy",
                                                                                     MockWebSocketEvent.SERVER_LISTENER,
                                                                                     MockWebSocketEvent.SERVER_PROCESSOR);
        Assertions.assertEquals(1, new WebSocketRouterCreator(Arrays.asList(metadata1, metadata2)).validate().size());
    }

    @Test
    public void test_register_many_metadata_with_different_path() {
        WebSocketServerEventMetadata metadata1 = WebSocketServerEventMetadata.create("xy",
                                                                                     MockWebSocketEvent.SERVER_LISTENER,
                                                                                     MockWebSocketEvent.SERVER_PROCESSOR);
        WebSocketServerEventMetadata metadata2 = WebSocketServerEventMetadata.create("abc",
                                                                                     MockWebSocketEvent.SERVER_LISTENER,
                                                                                     MockWebSocketEvent.SERVER_PROCESSOR);
        Assertions.assertEquals(2, new WebSocketRouterCreator(Arrays.asList(metadata1, metadata2)).validate().size());
    }

}

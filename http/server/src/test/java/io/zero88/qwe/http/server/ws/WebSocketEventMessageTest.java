package io.zero88.qwe.http.server.ws;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.Status;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.exceptions.InitializerError;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;

public class WebSocketEventMessageTest {

    @Test
    public void test_missing_address() {
        Assertions.assertThrows(InitializerError.class,
                                () -> WebSocketEventMessage.builder().type(BridgeEventType.REGISTER).build());
    }

    @Test
    public void test_missing_type() {
        Assertions.assertThrows(InitializerError.class, () -> WebSocketEventMessage.builder().address("test").build());
    }

    @Test
    public void test_serialize_missing_msg() throws JSONException {
        WebSocketEventMessage message = WebSocketEventMessage.builder()
                                                             .address("test")
                                                             .type(BridgeEventType.REGISTER)
                                                             .build();
        JSONAssert.assertEquals("{\"address\":\"test\",\"type\":\"register\"}", message.toJson().encode(),
                                JSONCompareMode.STRICT);
    }

    @Test
    public void test_serialize_success() throws JSONException {
        EventMessage eventMessage = EventMessage.success(EventAction.CREATE, new JsonObject().put("hello", "world"));
        WebSocketEventMessage message = WebSocketEventMessage.builder()
                                                             .address("test")
                                                             .type(BridgeEventType.SEND)
                                                             .body(eventMessage)
                                                             .build();
        JSONAssert.assertEquals("{\"address\":\"test\",\"type\":\"send\",\"body\":{\"status\":\"SUCCESS\"," +
                                "\"action\":\"CREATE\",\"data\":{\"hello\":\"world\"}}}", message.toJson().encode(),
                                JSONCompareMode.STRICT);
    }

    @Test
    public void test_deserialize_missing_msg() {
        WebSocketEventMessage message = new JsonObject("{\"address\":\"test\",\"type\":\"REGISTER\"}").mapTo(
            WebSocketEventMessage.class);
        Assertions.assertEquals("test", message.getAddress());
        Assertions.assertEquals(BridgeEventType.REGISTER, message.getType());
        Assertions.assertNull(message.getBody());
    }

    @Test
    public void test_deserialize_socketMsg_missing_event_action() {
        Assertions.assertThrows(CarlException.class, () -> WebSocketEventMessage.from(
            "{\"address\":\"test\",\"type\":\"SEND\",\"body\":{\"data\":{\"hello\":\"world\"}}}"));
    }

    @Test
    public void test_deserialize_socketMsg_unknown_type() {
        Assertions.assertThrows(CarlException.class,
                                () -> WebSocketEventMessage.from("{\"address\":\"test\",\"type\":\"xxx\"}"));
    }

    @Test
    public void test_deserialize_socketMsg_without_data() {
        WebSocketEventMessage from = WebSocketEventMessage.from(
            new JsonObject("{\"address\":\"socket.client2server\",\"type\":\"RECEIVE\"}"));
        Assertions.assertEquals(BridgeEventType.RECEIVE, from.getType());
        Assertions.assertEquals("socket.client2server", from.getAddress());
        Assertions.assertNull(from.getBody());
    }

    @Test
    public void test_deserialize_socketMsg_full() throws JSONException {
        WebSocketEventMessage message = WebSocketEventMessage.from(
            "{\"address\":\"test\",\"type\":\"rec\",\"body\":{" + "\"action\":\"CREATE\"," +
            "\"data\":{\"hello\":\"world\"}}}");
        Assertions.assertEquals("test", message.getAddress());
        Assertions.assertEquals(BridgeEventType.RECEIVE, message.getType());
        Assertions.assertNotNull(message.getBody());
        Assertions.assertEquals(EventAction.CREATE, message.getBody().getAction());
        Assertions.assertEquals(Status.INITIAL, message.getBody().getStatus());
        JSONAssert.assertEquals("{\"hello\":\"world\"}", message.getBody().getData().encode(), JSONCompareMode.STRICT);
        Assertions.assertNull(message.getBody().getError());
    }

    @Test
    public void test_deserialize_unknown_type() {
        Assertions.assertThrows(CarlException.class, () -> WebSocketEventMessage.from(
            "{\"address\":\"test\",\"type\":\"rec1\",\"body\":{" + "\"action\":\"CREATE\"}}"));
    }

}

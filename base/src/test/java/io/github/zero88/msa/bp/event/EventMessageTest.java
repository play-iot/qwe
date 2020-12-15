package io.github.zero88.msa.bp.event;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.vertx.core.json.JsonObject;

public class EventMessageTest {

    @Test
    public void test_EventMessage_Success() throws JSONException {
        EventMessage msg = EventMessage.success(EventAction.CREATE, new JsonObject(
            "{\"groupId\":\"io.zbpspark\",\"version\":\"1.0-SNAPSHOT\"}"));
        Assertions.assertFalse(msg.isError());
        Assertions.assertTrue(msg.isSuccess());
        Assertions.assertEquals(EventAction.CREATE, msg.getAction());
        Assertions.assertNull(msg.getError());
        JSONAssert.assertEquals("{\"status\":\"SUCCESS\",\"action\":\"CREATE\",\"" +
                                "data\":{\"groupId\":\"io.zbpspark\",\"version\":\"1.0-SNAPSHOT\"}}",
                                msg.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_EventMessage_Error() throws JSONException {
        EventMessage error = EventMessage.error(EventAction.REMOVE, new RuntimeException("xxx"));
        Assertions.assertTrue(error.isError());
        Assertions.assertFalse(error.isSuccess());
        Assertions.assertEquals(EventAction.REMOVE, error.getAction());
        Assertions.assertNotNull(error.getError());
        Assertions.assertNull(error.getData());
        JSONAssert.assertEquals("{\"status\":\"FAILED\",\"action\":\"REMOVE\"," +
                                "\"error\":{\"code\":\"UNKNOWN_ERROR\",\"message\":\"UNKNOWN_ERROR | Cause: xxx\"}}",
                                error.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_deserialize_missing_action() {
        final JsonObject json = new JsonObject("{\"data\":{\"groupId\":\"io.zbpspark\"}}");
        Assertions.assertThrows(BlueprintException.class, () -> EventMessage.tryParse(json));
    }

    @Test
    public void test_deserialize_success() {
        JsonObject jsonObject = new JsonObject("{\"action\":\"CREATE\",\"data\":{\"groupId\":\"io.zbpspark\"," +
                                               "\"artifactId\":\"zbp-edge-ditto-driver\"}}");
        EventMessage message = EventMessage.tryParse(jsonObject.getMap());
        Assertions.assertFalse(message.isError());
        Assertions.assertFalse(message.isSuccess());
        Assertions.assertEquals(EventAction.CREATE, message.getAction());
        Assertions.assertEquals("{\"groupId\":\"io.zbpspark\",\"artifactId\":\"zbp-edge-ditto-driver\"}",
                                message.getData().encode());
        Assertions.assertNull(message.getError());
    }

    @Test
    public void test_deserialize_success_none_data() {
        JsonObject jsonObject = new JsonObject("{\"status\":\"SUCCESS\",\"action\":\"CREATE\"}");
        EventMessage message = EventMessage.tryParse(jsonObject.getMap());
        Assertions.assertFalse(message.isError());
        Assertions.assertTrue(message.isSuccess());
        Assertions.assertEquals(EventAction.CREATE, message.getAction());
        Assertions.assertNull(message.getData());
        Assertions.assertNull(message.getError());
    }

    @Test
    public void test_deserialize_error_data() {
        JsonObject jsonObject = new JsonObject(
            "{\"status\":\"FAILED\",\"action\":\"REMOVE\",\"error\":{\"code\":\"UNKNOWN_ERROR\"," +
            "\"message\":\"UNKNOWN_ERROR | Cause: xxx\"}}");
        EventMessage message = EventMessage.tryParse(jsonObject);
        Assertions.assertTrue(message.isError());
        Assertions.assertFalse(message.isSuccess());
        Assertions.assertEquals(EventAction.REMOVE, message.getAction());
        Assertions.assertNull(message.getData());
        Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, message.getError().getCode());
        Assertions.assertEquals("UNKNOWN_ERROR | Cause: xxx", message.getError().getMessage());
        Assertions.assertNull(message.getError().getThrowable());
    }

}

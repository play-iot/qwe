package io.zero88.qwe.event;

import java.util.Arrays;
import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.exceptions.ErrorCode;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.exceptions.QWEException;

public class EventMessageTest {

    @Test
    public void test_EventMessage_Success() throws JSONException {
        EventMessage msg = EventMessage.success(EventAction.CREATE, new JsonObject(
            "{\"groupId\":\"io.qwespark\",\"version\":\"1.0-SNAPSHOT\"}"));
        Assertions.assertFalse(msg.isError());
        Assertions.assertTrue(msg.isSuccess());
        Assertions.assertEquals(EventAction.CREATE, msg.getAction());
        Assertions.assertNull(msg.getError());
        JSONAssert.assertEquals("{\"status\":\"SUCCESS\",\"action\":\"CREATE\",\"" +
                                "data\":{\"groupId\":\"io.qwespark\",\"version\":\"1.0-SNAPSHOT\"}}",
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
        final JsonObject json = new JsonObject("{\"data\":{\"groupId\":\"io.qwespark\"}}");
        Assertions.assertThrows(QWEException.class, () -> EventMessage.tryParse(json));
    }

    @Test
    public void test_deserialize_success() {
        JsonObject jsonObject = new JsonObject("{\"action\":\"CREATE\",\"data\":{\"groupId\":\"io.qwespark\"," +
                                               "\"artifactId\":\"qwe-edge-ditto-driver\"}}");
        EventMessage message = EventMessage.tryParse(jsonObject);
        Assertions.assertFalse(message.isError());
        Assertions.assertFalse(message.isSuccess());
        Assertions.assertEquals(EventAction.CREATE, message.getAction());
        Assertions.assertEquals("{\"groupId\":\"io.qwespark\",\"artifactId\":\"qwe-edge-ditto-driver\"}",
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

    @Test
    public void test_serialize_deserialize_requestData() {
        final JsonObject expected = new JsonObject("{\"status\":\"INITIAL\",\"action\":\"INIT\",\"data\":" +
                                                   "{\"headers\":{},\"body\":{\"1\":\"a\"},\"filter\":{}}}");
        final RequestData reqData = RequestData.builder().body(new JsonObject().put("1", "a")).build();
        final EventMessage msg = EventMessage.initial(EventAction.INIT, reqData);
        Assertions.assertEquals(expected, msg.toJson());
        Assertions.assertEquals(reqData.toJson(), msg.getData());
        Assertions.assertEquals(reqData, msg.parseAndGetData());
        final EventMessage deserialize = EventMessage.tryParse(msg.toJson());
        Assertions.assertTrue(deserialize.parseAndGetData() instanceof Map);
        Assertions.assertEquals(reqData, msg.parseAndGetData(RequestData.class));
        Assertions.assertEquals(msg.toJson(), deserialize.toJson());
    }

    @Test
    public void test_deserialize_array() {
        JsonObject expected = new JsonObject("{\"status\":\"SUCCESS\",\"action\":\"REMOVE\",\"data\":[1,2,3]}");
        EventMessage msg = EventMessage.tryParse(expected);
        Assertions.assertTrue(msg.isSuccess());
        Assertions.assertEquals(EventAction.REMOVE, msg.getAction());
        Assertions.assertNotNull(msg.rawData());
        Assertions.assertEquals(new JsonArray().add(1).add(2).add(3), msg.rawData().toJson());
        Assertions.assertEquals(Arrays.asList(1, 2, 3), msg.parseAndGetData());
        Assertions.assertEquals(new JsonObject().put("data", Arrays.asList(1, 2, 3)), msg.getData());
        Assertions.assertEquals(expected, msg.toJson());
    }

    @Test
    public void test_deserialize_string() {
        JsonObject expected = new JsonObject("{\"status\":\"SUCCESS\",\"action\":\"REMOVE\",\"data\":\"xyz\"}");
        EventMessage msg = EventMessage.tryParse(expected);
        Assertions.assertTrue(msg.isSuccess());
        Assertions.assertEquals(EventAction.REMOVE, msg.getAction());
        Assertions.assertNotNull(msg.rawData());
        Assertions.assertEquals("xyz", msg.rawData().toJson());
        Assertions.assertEquals("xyz", msg.parseAndGetData());
        Assertions.assertEquals(expected, msg.toJson());
        Assertions.assertEquals(new JsonObject().put("data", "xyz"), msg.getData());
    }
}

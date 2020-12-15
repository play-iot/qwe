package io.github.zero88.msa.bp.event;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
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
        Assert.assertFalse(msg.isError());
        Assert.assertTrue(msg.isSuccess());
        Assert.assertEquals(EventAction.CREATE, msg.getAction());
        Assert.assertNull(msg.getError());
        JSONAssert.assertEquals("{\"status\":\"SUCCESS\",\"action\":\"CREATE\",\"" +
                                "data\":{\"groupId\":\"io.zbpspark\",\"version\":\"1.0-SNAPSHOT\"}}",
                                msg.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_EventMessage_Error() throws JSONException {
        EventMessage error = EventMessage.error(EventAction.REMOVE, new RuntimeException("xxx"));
        Assert.assertTrue(error.isError());
        Assert.assertFalse(error.isSuccess());
        Assert.assertEquals(EventAction.REMOVE, error.getAction());
        Assert.assertNotNull(error.getError());
        Assert.assertNull(error.getData());
        JSONAssert.assertEquals("{\"status\":\"FAILED\",\"action\":\"REMOVE\"," +
                            "\"error\":{\"code\":\"UNKNOWN_ERROR\",\"message\":\"UNKNOWN_ERROR | Cause: xxx\"}}",
                            error.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test(expected = BlueprintException.class)
    public void test_deserialize_missing_action() {
        EventMessage.tryParse(new JsonObject("{\"data\":{\"groupId\":\"io.zbpspark\"}}"));
    }

    @Test
    public void test_deserialize_success() {
        JsonObject jsonObject = new JsonObject("{\"action\":\"CREATE\",\"data\":{\"groupId\":\"io.zbpspark\"," +
                                               "\"artifactId\":\"zbp-edge-ditto-driver\"}}");
        EventMessage message = EventMessage.tryParse(jsonObject.getMap());
        Assert.assertFalse(message.isError());
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals(EventAction.CREATE, message.getAction());
        Assert.assertEquals("{\"groupId\":\"io.zbpspark\",\"artifactId\":\"zbp-edge-ditto-driver\"}",
                            message.getData().encode());
        Assert.assertNull(message.getError());
    }

    @Test
    public void test_deserialize_success_none_data() {
        JsonObject jsonObject = new JsonObject("{\"status\":\"SUCCESS\",\"action\":\"CREATE\"}");
        EventMessage message = EventMessage.tryParse(jsonObject.getMap());
        Assert.assertFalse(message.isError());
        Assert.assertTrue(message.isSuccess());
        Assert.assertEquals(EventAction.CREATE, message.getAction());
        Assert.assertNull(message.getData());
        Assert.assertNull(message.getError());
    }

    @Test
    public void test_deserialize_error_data() {
        JsonObject jsonObject = new JsonObject(
            "{\"status\":\"FAILED\",\"action\":\"REMOVE\",\"error\":{\"code\":\"UNKNOWN_ERROR\"," +
            "\"message\":\"UNKNOWN_ERROR | Cause: xxx\"}}");
        EventMessage message = EventMessage.tryParse(jsonObject);
        Assert.assertTrue(message.isError());
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals(EventAction.REMOVE, message.getAction());
        Assert.assertNull(message.getData());
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, message.getError().getCode());
        Assert.assertEquals("UNKNOWN_ERROR | Cause: xxx", message.getError().getMessage());
        Assert.assertNull(message.getError().getThrowable());
    }

}

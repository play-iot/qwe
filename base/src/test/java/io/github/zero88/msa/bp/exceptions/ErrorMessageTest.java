package io.github.zero88.msa.bp.exceptions;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.msa.bp.dto.ErrorMessage;
import io.reactivex.exceptions.CompositeException;
import io.vertx.core.json.JsonObject;

public class ErrorMessageTest {

    @Test
    public void test_null_throwable() {
        Assertions.assertThrows(NullPointerException.class, () -> ErrorMessage.parse((Throwable) null));
    }

    @Test
    public void test_composite_exception() {
        final ErrorMessage message = ErrorMessage.parse(
            new CompositeException(new RuntimeException("1"), new RuntimeException("2")));
        Assertions.assertEquals(io.github.zero88.exceptions.ErrorCode.UNKNOWN_ERROR, message.getCode());
        Assertions.assertEquals("UNKNOWN_ERROR | Cause: 2", message.getMessage());
    }

    @Test
    public void test_composite_exception_include_blueprint_exception_not_last() {
        final ErrorMessage message = ErrorMessage.parse(
            new CompositeException(new NotFoundException("xxx"), new IllegalStateException("abc")));
        Assertions.assertEquals(io.github.zero88.exceptions.ErrorCode.UNKNOWN_ERROR, message.getCode());
        Assertions.assertEquals("UNKNOWN_ERROR | Cause: abc", message.getMessage());
    }

    @Test
    public void test_composite_exception_include_blueprint_exception_at_last() {
        final ErrorMessage message = ErrorMessage.parse(
            new CompositeException(new RuntimeException("1"), new NotFoundException("xxx")));
        Assertions.assertEquals(ErrorCode.NOT_FOUND, message.getCode());
        Assertions.assertEquals("xxx", message.getMessage());
    }

    @Test
    public void test_blueprint_exception() {
        final ErrorMessage message = ErrorMessage.parse(new BlueprintException(ErrorCode.INVALID_ARGUMENT, "invalid"));
        Assertions.assertEquals(ErrorCode.INVALID_ARGUMENT, message.getCode());
        Assertions.assertEquals("invalid", message.getMessage());
    }

    @Test
    public void test_unexpected_exception_with_message() {
        final ErrorMessage message = ErrorMessage.parse(new RuntimeException("hey"));
        Assertions.assertEquals(io.github.zero88.exceptions.ErrorCode.UNKNOWN_ERROR, message.getCode());
        Assertions.assertEquals("UNKNOWN_ERROR | Cause: hey", message.getMessage());
    }

    @Test
    public void test_unexpected_exception_with_no_message() {
        final ErrorMessage message = ErrorMessage.parse(new RuntimeException());
        Assertions.assertEquals(io.github.zero88.exceptions.ErrorCode.UNKNOWN_ERROR, message.getCode());
        Assertions.assertEquals("UNKNOWN_ERROR", message.getMessage());
    }

    @Test
    public void test_serialize_to_json() throws JSONException {
        ErrorMessage msg = ErrorMessage.parse(new BlueprintException(ErrorCode.INVALID_ARGUMENT, "invalid"));
        Assertions.assertNotNull(msg.getThrowable());
        JsonObject jsonMsg = msg.toJson();
        JSONAssert.assertEquals("{\"code\":\"INVALID_ARGUMENT\",\"message\":\"invalid\"}", jsonMsg.encode(),
                                JSONCompareMode.STRICT);
        ErrorMessage deserialize = jsonMsg.mapTo(ErrorMessage.class);
        Assertions.assertNull(deserialize.getThrowable());
        Assertions.assertEquals("invalid", deserialize.getMessage());
        Assertions.assertEquals(ErrorCode.INVALID_ARGUMENT, deserialize.getCode());
    }

}

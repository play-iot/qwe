package io.github.zero88.msa.bp.exceptions.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.EngineException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.msa.bp.exceptions.SecurityException;
import io.github.zero88.msa.bp.exceptions.ServiceException;

public class BlueprintExceptionConverterTest {

    private BlueprintExceptionConverter converter;

    @BeforeEach
    public void setup() {
        converter = new BlueprintExceptionConverter(true, null);
    }

    @Test
    public void test_null() {
        Assertions.assertThrows(NullPointerException.class, () -> converter.apply(null));
    }

    @Test
    public void test_only_code() {
        BlueprintException t = converter.apply(new BlueprintException(ErrorCode.SERVICE_ERROR));
        Assertions.assertEquals(ErrorCode.SERVICE_ERROR, t.errorCode());
        Assertions.assertNull(t.getMessage());
        Assertions.assertNull(t.getCause());
    }

    @Test
    public void test_only_message() {
        BlueprintException t = converter.apply(new BlueprintException("2"));
        Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
        Assertions.assertEquals("2", t.getMessage());
        Assertions.assertNull(t.getCause());
    }

    @Test
    public void test_only_throwable_has_message() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            BlueprintException t = converter.apply(new BlueprintException(new IllegalStateException("lorem")));
            Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
            Assertions.assertEquals("UNKNOWN_ERROR | Cause: lorem", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_only_throwable_no_message() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            BlueprintException t = converter.apply(new BlueprintException(new IllegalStateException()));
            Assertions.assertEquals(io.github.zero88.exceptions.ErrorCode.UNKNOWN_ERROR, t.errorCode());
            Assertions.assertEquals("UNKNOWN_ERROR", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_with_code_without_cause() {
        BlueprintException t = converter.apply(new BlueprintException(ErrorCode.EVENT_ERROR, "1"));
        Assertions.assertEquals(ErrorCode.EVENT_ERROR, t.errorCode());
        Assertions.assertEquals("1", t.getMessage());
        Assertions.assertNull(t.getCause());
    }

    @Test
    public void test_with_code_with_other_cause_no_message() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            BlueprintException t = converter.apply(
                new BlueprintException(ErrorCode.EVENT_ERROR, "1", new RuntimeException()));
            Assertions.assertEquals(ErrorCode.EVENT_ERROR, t.errorCode());
            Assertions.assertEquals("1", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_with_code_with_other_cause_has_message() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            BlueprintException t = converter.apply(
                new BlueprintException(ErrorCode.NOT_FOUND, "abc", new RuntimeException("xyz")));
            Assertions.assertEquals(ErrorCode.NOT_FOUND, t.errorCode());
            Assertions.assertEquals("abc | Cause: xyz", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_with_code_with_blueprint_cause() {
        Assertions.assertThrows(BlueprintException.class, () -> {
            BlueprintException t = converter.apply(new SecurityException("abc", new EngineException("xyz")));
            Assertions.assertEquals(SecurityException.CODE, t.errorCode());
            Assertions.assertEquals("abc | Cause: xyz - Error Code: ENGINE_ERROR", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_with_code_with_hidden_cause() {
        Assertions.assertThrows(HiddenException.class, () -> {
            BlueprintException t = converter.apply(
                new ServiceException("abc", new HiddenException(ErrorCode.EVENT_ERROR, "xyz", null)));
            Assertions.assertEquals(ErrorCode.SERVICE_ERROR, t.errorCode());
            Assertions.assertEquals("abc", t.getMessage());
            HiddenException cause = (HiddenException) t.getCause();
            Assertions.assertEquals(ErrorCode.EVENT_ERROR, cause.errorCode());
            Assertions.assertEquals("xyz", cause.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_other_exception_no_message() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            BlueprintException t = converter.apply(new IllegalStateException());
            Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
            Assertions.assertEquals("UNKNOWN_ERROR", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_invalid_argument_exception_no_message() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            BlueprintException t = converter.apply(
                new IllegalArgumentException("xx", new IllegalStateException("abc")));
            Assertions.assertEquals(ErrorCode.INVALID_ARGUMENT, t.errorCode());
            Assertions.assertEquals("xx", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_other_exception_has_message() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            BlueprintException t = converter.apply(new RuntimeException("xyz"));
            Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
            Assertions.assertEquals("UNKNOWN_ERROR | Cause: xyz", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_other_exception_bound_blueprint_exception() {
        BlueprintException t = converter.apply(new RuntimeException(new ServiceException("hey")));
        Assertions.assertEquals(ErrorCode.SERVICE_ERROR, t.errorCode());
        Assertions.assertEquals("hey", t.getMessage());
        Assertions.assertNull(t.getCause());
    }

}

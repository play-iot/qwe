package io.github.zero88.qwe.exceptions.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.EngineException;
import io.github.zero88.qwe.exceptions.ErrorCode;
import io.github.zero88.qwe.exceptions.SecurityException;
import io.github.zero88.qwe.exceptions.ServiceException;

public class CarlExceptionConverterTest {

    private CarlExceptionConverter converter;

    @BeforeEach
    public void setup() {
        converter = new CarlExceptionConverter(true, null);
    }

    @Test
    public void test_null() {
        Assertions.assertThrows(NullPointerException.class, () -> converter.apply(null));
    }

    @Test
    public void test_only_code() {
        CarlException t = converter.apply(new CarlException(ErrorCode.SERVICE_ERROR));
        Assertions.assertEquals(ErrorCode.SERVICE_ERROR, t.errorCode());
        Assertions.assertNull(t.getMessage());
        Assertions.assertNull(t.getCause());
    }

    @Test
    public void test_only_message() {
        CarlException t = converter.apply(new CarlException("2"));
        Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
        Assertions.assertEquals("2", t.getMessage());
        Assertions.assertNull(t.getCause());
    }

    @Test
    public void test_only_throwable_has_message() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            CarlException t = converter.apply(new CarlException(new IllegalStateException("lorem")));
            Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
            Assertions.assertEquals("UNKNOWN_ERROR | Cause: lorem", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_only_throwable_no_message() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            CarlException t = converter.apply(new CarlException(new IllegalStateException()));
            Assertions.assertEquals(io.github.zero88.exceptions.ErrorCode.UNKNOWN_ERROR, t.errorCode());
            Assertions.assertEquals("UNKNOWN_ERROR", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_with_code_without_cause() {
        CarlException t = converter.apply(new CarlException(ErrorCode.EVENT_ERROR, "1"));
        Assertions.assertEquals(ErrorCode.EVENT_ERROR, t.errorCode());
        Assertions.assertEquals("1", t.getMessage());
        Assertions.assertNull(t.getCause());
    }

    @Test
    public void test_with_code_with_other_cause_no_message() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            CarlException t = converter.apply(
                new CarlException(ErrorCode.EVENT_ERROR, "1", new RuntimeException()));
            Assertions.assertEquals(ErrorCode.EVENT_ERROR, t.errorCode());
            Assertions.assertEquals("1", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_with_code_with_other_cause_has_message() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            CarlException t = converter.apply(
                new CarlException(ErrorCode.NOT_FOUND, "abc", new RuntimeException("xyz")));
            Assertions.assertEquals(ErrorCode.NOT_FOUND, t.errorCode());
            Assertions.assertEquals("abc | Cause: xyz", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_with_code_with_carl_cause() {
        Assertions.assertThrows(CarlException.class, () -> {
            CarlException t = converter.apply(new SecurityException("abc", new EngineException("xyz")));
            Assertions.assertEquals(SecurityException.CODE, t.errorCode());
            Assertions.assertEquals("abc | Cause: xyz - Error Code: ENGINE_ERROR", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_with_code_with_hidden_cause() {
        Assertions.assertThrows(HiddenException.class, () -> {
            CarlException t = converter.apply(
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
            CarlException t = converter.apply(new IllegalStateException());
            Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
            Assertions.assertEquals("UNKNOWN_ERROR", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_invalid_argument_exception_no_message() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            CarlException t = converter.apply(
                new IllegalArgumentException("xx", new IllegalStateException("abc")));
            Assertions.assertEquals(ErrorCode.INVALID_ARGUMENT, t.errorCode());
            Assertions.assertEquals("xx", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_other_exception_has_message() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            CarlException t = converter.apply(new RuntimeException("xyz"));
            Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
            Assertions.assertEquals("UNKNOWN_ERROR | Cause: xyz", t.getMessage());
            throw t.getCause();
        });
    }

    @Test
    public void test_other_exception_bound_carl_exception() {
        CarlException t = converter.apply(new RuntimeException(new ServiceException("hey")));
        Assertions.assertEquals(ErrorCode.SERVICE_ERROR, t.errorCode());
        Assertions.assertEquals("hey", t.getMessage());
        Assertions.assertNull(t.getCause());
    }

}

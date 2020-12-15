package io.github.zero88.msa.bp.exceptions.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.EngineException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.msa.bp.exceptions.SecurityException;
import io.github.zero88.msa.bp.exceptions.ServiceException;

public class BlueprintExceptionConverterTest {

    private BlueprintExceptionConverter converter;

    @Before
    public void setup() {
        converter = new BlueprintExceptionConverter(true, null);
    }

    @Test(expected = NullPointerException.class)
    public void test_null() {
        converter.apply(null);
    }

    @Test
    public void test_only_code() {
        BlueprintException t = converter.apply(new BlueprintException(ErrorCode.SERVICE_ERROR));
        Assert.assertEquals(ErrorCode.SERVICE_ERROR, t.errorCode());
        Assert.assertNull(t.getMessage());
        Assert.assertNull(t.getCause());
    }

    @Test
    public void test_only_message() {
        BlueprintException t = converter.apply(new BlueprintException("2"));
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
        Assert.assertEquals("2", t.getMessage());
        Assert.assertNull(t.getCause());
    }

    @Test(expected = IllegalStateException.class)
    public void test_only_throwable_has_message() throws Throwable {
        BlueprintException t = converter.apply(new BlueprintException(new IllegalStateException("lorem")));
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
        Assert.assertEquals("UNKNOWN_ERROR | Cause: lorem", t.getMessage());
        throw t.getCause();
    }

    @Test(expected = IllegalStateException.class)
    public void test_only_throwable_no_message() throws Throwable {
        BlueprintException t = converter.apply(new BlueprintException(new IllegalStateException()));
        Assert.assertEquals(io.github.zero88.exceptions.ErrorCode.UNKNOWN_ERROR, t.errorCode());
        Assert.assertEquals("UNKNOWN_ERROR", t.getMessage());
        throw t.getCause();
    }

    @Test
    public void test_with_code_without_cause() {
        BlueprintException t = converter.apply(new BlueprintException(ErrorCode.EVENT_ERROR, "1"));
        Assert.assertEquals(ErrorCode.EVENT_ERROR, t.errorCode());
        Assert.assertEquals("1", t.getMessage());
        Assert.assertNull(t.getCause());
    }

    @Test(expected = RuntimeException.class)
    public void test_with_code_with_other_cause_no_message() throws Throwable {
        BlueprintException t = converter.apply(
            new BlueprintException(ErrorCode.EVENT_ERROR, "1", new RuntimeException()));
        Assert.assertEquals(ErrorCode.EVENT_ERROR, t.errorCode());
        Assert.assertEquals("1", t.getMessage());
        throw t.getCause();
    }

    @Test(expected = RuntimeException.class)
    public void test_with_code_with_other_cause_has_message() throws Throwable {
        BlueprintException t = converter.apply(
            new BlueprintException(ErrorCode.HTTP_ERROR, "abc", new RuntimeException("xyz")));
        Assert.assertEquals(ErrorCode.HTTP_ERROR, t.errorCode());
        Assert.assertEquals("abc | Cause: xyz", t.getMessage());
        throw t.getCause();
    }

    @Test(expected = BlueprintException.class)
    public void test_with_code_with_blueprint_cause() throws Throwable {
        BlueprintException t = converter.apply(new SecurityException("abc", new EngineException("xyz")));
        Assert.assertEquals(ErrorCode.SECURITY_ERROR, t.errorCode());
        Assert.assertEquals("abc | Cause: xyz - Error Code: ENGINE_ERROR", t.getMessage());
        throw t.getCause();
    }

    @Test(expected = HiddenException.class)
    public void test_with_code_with_hidden_cause() throws Throwable {
        BlueprintException t = converter.apply(
            new ServiceException("abc", new HiddenException(ErrorCode.EVENT_ERROR, "xyz", null)));
        Assert.assertEquals(ErrorCode.SERVICE_ERROR, t.errorCode());
        Assert.assertEquals("abc", t.getMessage());
        HiddenException cause = (HiddenException) t.getCause();
        Assert.assertEquals(ErrorCode.EVENT_ERROR, cause.errorCode());
        Assert.assertEquals("xyz", cause.getMessage());
        throw t.getCause();
    }

    @Test(expected = IllegalStateException.class)
    public void test_other_exception_no_message() throws Throwable {
        BlueprintException t = converter.apply(new IllegalStateException());
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
        Assert.assertEquals("UNKNOWN_ERROR", t.getMessage());
        throw t.getCause();
    }

    @Test(expected = IllegalStateException.class)
    public void test_invalid_argument_exception_no_message() throws Throwable {
        BlueprintException t = converter.apply(new IllegalArgumentException("xx", new IllegalStateException("abc")));
        Assert.assertEquals(ErrorCode.INVALID_ARGUMENT, t.errorCode());
        Assert.assertEquals("xx", t.getMessage());
        throw t.getCause();
    }

    @Test(expected = RuntimeException.class)
    public void test_other_exception_has_message() throws Throwable {
        BlueprintException t = converter.apply(new RuntimeException("xyz"));
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, t.errorCode());
        Assert.assertEquals("UNKNOWN_ERROR | Cause: xyz", t.getMessage());
        throw t.getCause();
    }

    @Test
    public void test_other_exception_bound_blueprint_exception() {
        BlueprintException t = converter.apply(new RuntimeException(new ServiceException("hey")));
        Assert.assertEquals(ErrorCode.SERVICE_ERROR, t.errorCode());
        Assert.assertEquals("hey", t.getMessage());
        Assert.assertNull(t.getCause());
    }

}

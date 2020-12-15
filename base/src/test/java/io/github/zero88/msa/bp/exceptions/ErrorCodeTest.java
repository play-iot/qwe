package io.github.zero88.msa.bp.exceptions;

import org.junit.Assert;
import org.junit.Test;

public class ErrorCodeTest {
    @Test
    public void test(){
        final ErrorCode error = ErrorCode.parse("UNKNOWN_ERROR");
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, error);
    }
}

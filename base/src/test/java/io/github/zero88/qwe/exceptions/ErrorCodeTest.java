package io.github.zero88.qwe.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ErrorCodeTest {

    @Test
    public void test() {
        final ErrorCode error = ErrorCode.parse("UNKNOWN_ERROR");
        Assertions.assertEquals(ErrorCode.UNKNOWN_ERROR, error);
    }

}

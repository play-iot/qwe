package cloud.playio.qwe.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ErrorCodeTest {

    @Test
    public void test() {
        final ErrorCode error = ErrorCode.parse("UNKNOWN_ERROR");
        Assertions.assertEquals(io.github.zero88.exceptions.ErrorCode.UNKNOWN_ERROR, error);
    }

}

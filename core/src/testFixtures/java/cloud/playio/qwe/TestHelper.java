package cloud.playio.qwe;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import io.github.zero88.utils.Strings;
import io.vertx.core.Handler;
import io.vertx.ext.unit.Async;

public interface TestHelper extends HasLogger {

    int TEST_TIMEOUT_SEC = 8;

    Logger LOGGER = HasLogger.getLogger("LOG_TEST");

    @Override
    default Logger logger() {
        return LOGGER;
    }

    static int getRandomPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void setup() {
        setup(Level.DEBUG);
    }

    static void setup(Level projLogLvl) {
        System.setProperty("vertx.logger-delegate-factory-class-name",
                           "io.vertx.core.logging.Log4j2LogDelegateFactory");
        Configurator.setRootLevel(Level.INFO);
        Configurator.setLevel("cloud.playio.qwe", projLogLvl);
    }

    static void testComplete(Async async) {
        testComplete(async, "", null);
    }

    static void testComplete(Async async, String msgEvent, Handler<Void> completeAction) {
        LOGGER.info("Current Test Async Count: {}. Countdown...", async.count());
        if (Strings.isBlank(msgEvent)) {
            LOGGER.debug(msgEvent);
        }
        if (async.count() > 0) {
            async.countDown();
        }
        if (async.count() == 0 && !async.isCompleted()) {
            async.complete();
            if (Objects.nonNull(completeAction)) {
                completeAction.handle(null);
            }
        }
    }

    static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static <T extends Throwable> void assertCause(Executable ex, Class<T> throwableClass,
                                                  Class<? extends Throwable> causeClass) {
        TestHelper.assertCause(ex, throwableClass, causeClass, null);
    }

    static <T extends Throwable> void assertCause(Executable ex, Class<T> throwableClass,
                                                  Class<? extends Throwable> causeClass, String throwMsg) {
        Assertions.assertNotNull(assertCause(Assertions.assertThrows(throwableClass, ex, throwMsg), causeClass));
    }

    static <T extends Throwable> void assertCause(Executable ex, Class<T> throwable,
                                                  Class<? extends Throwable> causeClass, String throwMsg,
                                                  String causeMsg) {
        Throwable cause = assertCause(Assertions.assertThrows(throwable, ex, throwMsg), causeClass);
        Assertions.assertNotNull(cause);
        Assertions.assertEquals(causeMsg, cause.getMessage());
    }

    static Throwable assertCause(Throwable ex, Class<? extends Throwable> causeClass) {
        ex.printStackTrace(System.err);
        Assertions.assertTrue(causeClass.isInstance(ex.getCause()));
        return ex.getCause();
    }

    static void assertThrow(Throwable ex, Class<? extends Throwable> eClass) {
        ex.printStackTrace(System.err);
        Assertions.assertTrue(eClass.isInstance(ex));
    }

    static void waitTimeout(int timeout, CountDownLatch latch) {
        waitTimeout(timeout, latch, null);
    }

    static void waitTimeout(int timeout, CountDownLatch latch, Handler<Throwable> ifFailed) {
        try {
            Assertions.assertTrue(latch.await(timeout, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Optional.ofNullable(ifFailed)
                    .orElseGet(() -> t -> Assertions.fail("Interrupted when waiting timeout [" + timeout + "(s)]", t))
                    .handle(e);
        }
    }

}

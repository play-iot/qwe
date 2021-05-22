package io.zero88.qwe;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.ext.unit.Async;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public interface TestHelper {

    int TEST_TIMEOUT_SEC = 8;

    static int getRandomPort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    static void setup() {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);
        ((Logger) LoggerFactory.getLogger("io.github.zero88")).setLevel(Level.DEBUG);
    }

    static void testComplete(Async async) {
        testComplete(async, "", null);
    }

    static void testComplete(Async async, String msgEvent, Handler<Void> completeAction) {
        System.out.println("Current Test Async Count: " + async.count() + ". Countdown...");
        System.out.println(msgEvent);
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

    static <T extends Throwable> void assertThrows(Executable ex, Class<T> throwable,
                                                   Class<? extends Throwable> causeClass) {
        assertThrows(Assertions.assertThrows(throwable, ex), causeClass);
    }

    static void assertThrows(Throwable ex, Class<? extends Throwable> causeClass) {
        ex.printStackTrace(System.out);
        Assertions.assertTrue(causeClass.isInstance(ex.getCause()));
    }

}

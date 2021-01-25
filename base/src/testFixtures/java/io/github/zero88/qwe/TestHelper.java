package io.github.zero88.qwe;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.json.JSONException;
import org.skyscreamer.jsonassert.ArrayValueMatcher;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.dto.JsonData;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.NonNull;

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

    static void testComplete(io.vertx.reactivex.ext.unit.Async async) {
        testComplete(async.getDelegate());
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

}

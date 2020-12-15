package io.github.zero88.msa.bp;

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

import io.github.zero88.msa.bp.component.UnitVerticle;
import io.github.zero88.msa.bp.component.UnitVerticleTestHelper;
import io.github.zero88.msa.bp.dto.JsonData;
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
        ((Logger) LoggerFactory.getLogger("io.zero88")).setLevel(Level.DEBUG);
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

    interface VertxHelper {

        static <T extends Verticle> T deploy(@NonNull Vertx vertx, @NonNull TestContext context,
                                             @NonNull DeploymentOptions options, @NonNull T verticle,
                                             @NonNull Handler<String> handlerSuccess) {
            if (verticle instanceof UnitVerticle) {
                UnitVerticleTestHelper.injectTest((UnitVerticle) verticle, verticle.getClass().getName(), null);
            }
            vertx.deployVerticle(verticle, options, context.asyncAssertSuccess(handlerSuccess));
            return verticle;
        }

        static <T extends Verticle> T deploy(Vertx vertx, TestContext context, DeploymentOptions options, T verticle) {
            return deploy(vertx, context, options, verticle, TEST_TIMEOUT_SEC);
        }

        static <T extends Verticle> T deploy(Vertx vertx, TestContext context, DeploymentOptions options, T verticle,
                                             int timeout) {
            return deploy(vertx, context, options, verticle, timeout,
                          id -> System.out.println("Success deploy verticle: " + verticle.getClass() + " | ID: " + id));
        }

        static <T extends Verticle> T deploy(@NonNull Vertx vertx, @NonNull TestContext context,
                                             @NonNull DeploymentOptions options, @NonNull T verticle, int timeout,
                                             @NonNull Handler<String> handlerSuccess) {
            if (verticle instanceof UnitVerticle) {
                UnitVerticleTestHelper.injectTest((UnitVerticle) verticle, verticle.getClass().getName(), null);
            }
            CountDownLatch latch = new CountDownLatch(1);
            vertx.deployVerticle(verticle, options, context.asyncAssertSuccess(id -> {
                latch.countDown();
                handlerSuccess.handle(id);
            }));
            try {
                context.assertTrue(latch.await(timeout, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                context.fail(e);
            }
            return verticle;
        }

        static <T extends Verticle> void deployFailed(Vertx vertx, TestContext context, DeploymentOptions options,
                                                      T verticle, Handler<Throwable> errorHandler) {
            if (verticle instanceof UnitVerticle) {
                UnitVerticleTestHelper.injectTest((UnitVerticle) verticle, verticle.getClass().getName(), null);
            }
            vertx.deployVerticle(verticle, options, context.asyncAssertFailure(errorHandler));
        }

    }


    interface EventbusHelper {

        static Handler<AsyncResult<Message<Object>>> replyAsserter(TestContext context, Async async,
                                                                   JsonObject expected) {
            return replyAsserter(context, async, expected, JSONCompareMode.STRICT);
        }

        static Handler<AsyncResult<Message<Object>>> replyAsserter(TestContext context, Async async,
                                                                   JsonObject expected, JSONCompareMode mode) {
            return context.asyncAssertSuccess(
                result -> JsonHelper.assertJson(context, async, expected, (JsonObject) result.body(), mode));
        }

        static Handler<AsyncResult<Message<Object>>> replyAsserter(TestContext context, Async async,
                                                                   JsonObject expected,
                                                                   Customization... customizations) {
            return context.asyncAssertSuccess(
                result -> JsonHelper.assertJson(context, async, expected, (JsonObject) result.body(), customizations));
        }

        static Handler<AsyncResult<Message<Object>>> replyAsserter(TestContext context,
                                                                   Handler<JsonObject> bodyAsserter) {
            return context.asyncAssertSuccess(result -> bodyAsserter.handle((JsonObject) result.body()));
        }

        static void assertReceivedData(Vertx vertx, Async async, String address, Consumer<Object> assertData) {
            assertReceivedData(vertx, async, address, assertData, null);
        }

        static void assertReceivedData(Vertx vertx, Async async, String address, Consumer<Object> assertData,
                                       Handler<Void> testCompleted) {
            MessageConsumer<Object> consumer = vertx.eventBus().consumer(address);
            consumer.handler(event -> {
                System.out.println("Received message from address: " + address);
                assertData.accept(event.body());
                consumer.unregister(v -> testComplete(async, "CONSUMER END", testCompleted));
            });
        }

    }


    interface JsonHelper {

        static Customization ignore(@NonNull String path) {
            return new Customization(path, (o1, o2) -> true);
        }

        static Customization ignoreInArray(@NonNull String path, @NonNull String arrayPath) {
            ArrayValueMatcher<Object> arrValMatch = new ArrayValueMatcher<>(
                new CustomComparator(JSONCompareMode.NON_EXTENSIBLE, new Customization(path, (o1, o2) -> true)));
            return new Customization(arrayPath, arrValMatch);
        }

        static CustomComparator comparator(Customization... customizations) {
            return new CustomComparator(JSONCompareMode.LENIENT, customizations);
        }

        static Consumer<Object> asserter(TestContext context, Async async, JsonObject expected) {
            return resp -> JsonHelper.assertJson(context, async, expected, (JsonObject) resp, JSONCompareMode.STRICT);
        }

        static Consumer<Object> asserter(TestContext context, Async async, JsonObject expected, JSONCompareMode mode) {
            return resp -> JsonHelper.assertJson(context, async, expected, (JsonObject) resp, mode);
        }

        static void assertJson(JsonObject expected, JsonObject actual) throws JSONException {
            assertJson(expected, actual, JSONCompareMode.STRICT);
        }

        static void assertJson(JsonObject expected, JsonObject actual, Customization... customizations)
            throws JSONException {
            try {
                JSONAssert.assertEquals(expected.encode(), actual.encode(), comparator(customizations));
            } catch (JSONException | AssertionError e) {
                System.out.println("Actual: " + actual.encode());
                System.out.println("Expected: " + expected.encode());
                throw e;
            }
        }

        static void assertJson(JsonObject expected, JsonObject actual, JSONCompareMode mode) throws JSONException {
            try {
                JSONAssert.assertEquals(expected.encode(), actual.encode(), mode);
            } catch (JSONException | AssertionError e) {
                System.out.println("Actual: " + actual.encode());
                System.out.println("Expected: " + expected.encode());
                throw e;
            }
        }

        static void assertJson(TestContext context, Async async, JsonObject expected, Buffer buffer) {
            assertJson(context, async, expected, JsonData.tryParse(buffer).toJson());
        }

        static void assertJson(TestContext context, Async async, JsonObject expected, JsonObject actual,
                               JSONCompareMode mode) {
            try {
                JSONAssert.assertEquals(expected.encode(), actual.encode(), mode);
                System.out.println("Actual: " + actual.encode());
            } catch (JSONException | AssertionError e) {
                System.out.println("Actual: " + actual.encode());
                System.out.println("Expected: " + expected.encode());
                context.fail(e);
            } finally {
                testComplete(async);
            }
        }

        static void assertJson(TestContext context, Async async, JsonObject expected, JsonObject actual,
                               List<Customization> customizations) {
            assertJson(context, async, expected, actual,
                       Optional.ofNullable(customizations).orElse(new ArrayList<>()).toArray(new Customization[] {}));
        }

        static void assertJson(TestContext context, Async async, JsonObject expected, JsonObject actual,
                               Customization... customizations) {
            if (customizations.length == 0) {
                assertJson(context, async, expected, actual, JSONCompareMode.STRICT);
                return;
            }
            try {
                JSONAssert.assertEquals(expected.encode(), actual.encode(), comparator(customizations));
            } catch (JSONException | AssertionError e) {
                System.out.println("Actual: " + actual.encode());
                System.out.println("Expected: " + expected.encode());
                context.fail(e);
            } finally {
                testComplete(async);
            }
        }

    }

}

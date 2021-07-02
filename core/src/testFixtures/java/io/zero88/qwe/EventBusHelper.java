package io.zero88.qwe;

import java.util.function.Consumer;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.zero88.qwe.JsonHelper.Junit4;

public interface EventBusHelper {

    static Handler<AsyncResult<Message<Object>>> replyAsserter(TestContext context, Async async, JsonObject expected) {
        return replyAsserter(context, async, expected, JSONCompareMode.STRICT);
    }

    static Handler<AsyncResult<Message<Object>>> replyAsserter(TestContext context, Async async, JsonObject expected,
                                                               JSONCompareMode mode) {
        return context.asyncAssertSuccess(
            result -> Junit4.assertJson(context, async, expected, (JsonObject) result.body(), mode));
    }

    static Handler<AsyncResult<Message<Object>>> replyAsserter(TestContext context, Async async, JsonObject expected,
                                                               Customization... customizations) {
        return context.asyncAssertSuccess(
            result -> Junit4.assertJson(context, async, expected, (JsonObject) result.body(), customizations));
    }

    static Handler<AsyncResult<Message<Object>>> replyAsserter(TestContext context, Handler<JsonObject> bodyAsserter) {
        return context.asyncAssertSuccess(result -> bodyAsserter.handle((JsonObject) result.body()));
    }

    static void registerAssertReceivedData(Vertx vertx, Async async, String address, Consumer<Object> assertData) {
        registerAssertReceivedData(vertx, async, address, assertData, null);
    }

    static void registerAssertReceivedData(Vertx vertx, Async async, String address, Consumer<Object> assertData,
                                           Handler<Void> testCompleted) {
        MessageConsumer<Object> consumer = vertx.eventBus().consumer(address);
        consumer.handler(event -> {
            TestHelper.LOGGER.info("Received message from address [{}]", address);
            assertData.accept(event.body());
            consumer.unregister(v -> TestHelper.testComplete(async, "CONSUMER END", testCompleted));
        });
    }

}

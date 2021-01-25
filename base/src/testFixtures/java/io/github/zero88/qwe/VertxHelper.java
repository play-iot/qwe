package io.github.zero88.qwe;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;

import lombok.NonNull;

public interface VertxHelper {

    static <T extends Verticle> T deploy(@NonNull Vertx vertx, @NonNull TestContext context,
                                         @NonNull DeploymentOptions options, @NonNull T verticle,
                                         @NonNull Handler<String> handlerSuccess) {
        vertx.deployVerticle(verticle, options, context.asyncAssertSuccess(handlerSuccess));
        return verticle;
    }

    static <T extends Verticle> T deploy(Vertx vertx, TestContext context, DeploymentOptions options, T verticle) {
        return deploy(vertx, context, options, verticle, TestHelper.TEST_TIMEOUT_SEC);
    }

    static <T extends Verticle> T deploy(Vertx vertx, TestContext context, DeploymentOptions options, T verticle,
                                         int timeout) {
        return deploy(vertx, context, options, verticle, timeout,
                      id -> System.out.println("Success deploy verticle: " + verticle.getClass() + " | ID: " + id));
    }

    static <T extends Verticle> T deploy(@NonNull Vertx vertx, @NonNull TestContext context,
                                         @NonNull DeploymentOptions options, @NonNull T verticle, int timeout,
                                         @NonNull Handler<String> handlerSuccess) {
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
        vertx.deployVerticle(verticle, options, context.asyncAssertFailure(errorHandler));
    }

}

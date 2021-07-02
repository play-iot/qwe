package io.zero88.qwe;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.junit5.VertxTestContext;

import lombok.NonNull;

public interface VertxHelper {

    static <T extends Verticle> T deploy(@NonNull Vertx vertx, @NonNull TestContext context,
                                         @NonNull DeployContext<T> deployContext) {
        return doDeploy(vertx, deployContext, context::fail);
    }

    static <T extends Verticle> T deploy(@NonNull Vertx vertx, @NonNull VertxTestContext context,
                                         @NonNull DeployContext<T> deployContext) {
        return doDeploy(vertx, deployContext, context::failNow);
    }

    static <T extends Verticle> T doDeploy(Vertx vertx, DeployContext<T> deployContext, Handler<Throwable> ifFailed) {
        CountDownLatch latch = new CountDownLatch(1);
        vertx.deployVerticle(deployContext.verticle(), deployContext.options(), ar -> {
            try {
                if (ar.failed()) {
                    if (Objects.nonNull(deployContext.failedAsserter())) {
                        deployContext.failedAsserter().accept(ar.cause());
                    } else {
                        ifFailed.handle(ar.cause());
                    }
                } else {
                    deployContext.successAsserter().accept(ar.result());
                }
            } catch (Exception e) {
                ifFailed.handle(e);
            } finally {
                latch.countDown();
            }
        });
        waitTimeout(deployContext.timeout(), latch, ifFailed);
        return deployContext.verticle();
    }

    static <T extends Verticle> void deployFailed(Vertx vertx, TestContext context, DeploymentOptions options,
                                                  T verticle, Handler<Throwable> errorHandler) {
        vertx.deployVerticle(verticle, options, context.asyncAssertFailure(errorHandler));
    }

    static void waitTimeout(int timeout, CountDownLatch latch, Handler<Throwable> ifFailed) {
        try {
            Assertions.assertTrue(latch.await(timeout, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            ifFailed.handle(e);
        }
    }

}

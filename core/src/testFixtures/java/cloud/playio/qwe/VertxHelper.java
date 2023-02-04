package cloud.playio.qwe;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import io.github.zero88.utils.Strings;
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
        final T verticle = deployContext.verticle();
        TestHelper.LOGGER.info("START DEPLOYING VERTICLE[{}]", verticle.getClass().getName());
        TestHelper.LOGGER.info(Strings.duplicate("-", 150));
        long start = System.nanoTime();
        CountDownLatch latch = new CountDownLatch(1);
        vertx.deployVerticle(verticle, deployContext.options(), ar -> {
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
        TestHelper.waitTimeout(deployContext.timeout(), latch, ifFailed);
        TestHelper.LOGGER.info("DEPLOYED VERTICLE[{}] AFTER [{}]ms", verticle.getClass().getName(),
                               (System.nanoTime() - start) / 1e6);
        TestHelper.LOGGER.info(Strings.duplicate("=", 150));
        return verticle;
    }

    static <T extends Verticle> void deployFailed(Vertx vertx, TestContext context, DeploymentOptions options,
                                                  T verticle, Handler<Throwable> errorHandler) {
        vertx.deployVerticle(verticle, options, context.asyncAssertFailure(errorHandler));
    }

}

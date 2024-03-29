package cloud.playio.qwe.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.reactivex.Single;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class Rx2HelpersTest {

    public static String block() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Thread.currentThread().getName();
    }

    @Test
    public void test_workerThread(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() {
                Rx2Helpers.blocking(vertx, Rx2HelpersTest::block).subscribe(thread -> {
                    System.out.println("Thread: " + thread);
                    Assertions.assertEquals("vert.x-worker-thread-0", thread);
                    testContext.completeNow();
                });
            }
        });
    }

    @Test
    public void test_eventLoopThread(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() {
                Single.fromCallable(Rx2HelpersTest::block).subscribe(thread -> {
                    System.out.println("Thread: " + thread);
                    Assertions.assertEquals("vert.x-eventloop-thread-0", thread);
                    testContext.completeNow();
                });
            }
        });
    }

}

package io.github.zero88.msa.blueprint.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Single;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ExecutorHelpersTest {

    private Vertx vertx;

    public static String block() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Thread.currentThread().getName();
    }

    @Before
    public void setup() {
        vertx = Vertx.vertx();
    }

    @Test
    public void test_workerThread(final TestContext testContext) {
        Async async = testContext.async();
        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() {
                ExecutorHelpers.blocking(vertx, ExecutorHelpersTest::block).subscribe(thread -> {
                    System.out.println("Thread: " + thread);
                    testContext.assertEquals("vert.x-worker-thread-0", thread);
                    async.complete();
                });
            }
        });
    }

    @Test
    public void test_eventLoopThread(final TestContext testContext) {
        Async async = testContext.async();
        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() {
                Single.fromCallable(ExecutorHelpersTest::block).subscribe(thread -> {
                    System.out.println("Thread: " + thread);
                    testContext.assertEquals("vert.x-eventloop-thread-0", thread);
                    async.complete();
                });
            }
        });
    }

}

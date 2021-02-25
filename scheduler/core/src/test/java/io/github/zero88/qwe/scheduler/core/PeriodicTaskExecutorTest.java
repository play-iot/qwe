package io.github.zero88.qwe.scheduler.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.zero88.qwe.TestHelper;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.scheduler.core.TaskExecutorMonitor.TaskExecutorNoMonitor;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import lombok.NonNull;

@ExtendWith(VertxExtension.class)
class PeriodicTaskExecutorTest {

    @Test
    void test_run_task_in_the_end(Vertx vertx, VertxTestContext testContext) {
        final Checkpoint checkpoint = testContext.checkpoint(3);
        final WorkerExecutor worker = vertx.createSharedWorkerExecutor("TEST_PERIODIC", 3);
        final Task task = ctx -> {
            System.out.println("ON EXECUTE:: " + Thread.currentThread().getName() + "::" + ctx.round());
            TestHelper.sleep(4000);
            checkpoint.flag();
        };
        final TaskExecutorMonitor monitor = new TaskExecutorNoMonitor() {

            @Override
            public void onCompleted(@NonNull TaskResult data) {
                System.out.println("ON COMPLETED::" + Thread.currentThread().getName() + "::" + data.getRound());
                Assertions.assertEquals(3, data.getRound());
                Assertions.assertTrue(data.isCompleted());
                Assertions.assertFalse(data.isError());
            }
        };
        PeriodicTaskExecutor.builder()
                            .vertx(vertx)
                            .interval(2)
                            .repeat(3)
                            .task(task)
                            .monitor(monitor)
                            .build()
                            .start(worker);
    }

    @Test
    void test_cancel_task_in_condition(Vertx vertx, VertxTestContext testContext) {
        final Checkpoint checkpoint = testContext.checkpoint(5);
        final Task task = ctx -> {
            checkpoint.flag();
            final long round = ctx.round();
            System.out.println("ON EXECUTE:: " + Thread.currentThread().getName() + "::" + ctx.round());
            if (round == 2) {
                throw new RuntimeException("xx");
            }
            if (round == 4) {
                throw new CarlException("yy");
            }
            if (round == 5) {
                ctx.forceStopExecution();
            }
        };
        final TaskExecutorMonitor monitor = new TaskExecutorNoMonitor() {

            @Override
            public void onEach(@NonNull TaskResult data) {
                if (data.getRound() == 2) {
                    Assertions.assertTrue(data.isError());
                    Assertions.assertNotNull(data.getError());
                    Assertions.assertTrue(data.getError() instanceof RuntimeException);
                }
                if (data.getRound() == 4) {
                    Assertions.assertTrue(data.isError());
                    Assertions.assertNotNull(data.getError());
                    Assertions.assertTrue(data.getError() instanceof CarlException);
                }
            }

            @Override
            public void onCompleted(@NonNull TaskResult data) {
                System.out.println("ON COMPLETED::" + Thread.currentThread().getName() + "::" + data.getRound());
                Assertions.assertEquals(5, data.getRound());
                Assertions.assertTrue(data.isCompleted());
                Assertions.assertFalse(data.isError());
            }
        };
        PeriodicTaskExecutor.builder().vertx(vertx).interval(1).repeat(10L).task(task).monitor(monitor).build().start();
    }

}

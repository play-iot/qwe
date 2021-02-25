package io.github.zero88.qwe.scheduler.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.zero88.qwe.TestHelper;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.scheduler.core.impl.IntervalTaskExecutor;
import io.github.zero88.qwe.scheduler.core.trigger.IntervalTrigger;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import lombok.NonNull;

@ExtendWith(VertxExtension.class)
class IntervalTaskExecutorTest {

    private TaskExecutorMonitor unableScheduleAsserter(VertxTestContext testContext, Checkpoint checkpoint) {
        return new TaskExecutorLogMonitor() {
            @Override
            public void unableSchedule(@NonNull TaskResult result) {
                testContext.verify(() -> {
                    checkpoint.flag();
                    Assertions.assertNotNull(result.availableAt());
                    Assertions.assertTrue(result.error() instanceof IllegalArgumentException);
                    testContext.completeNow();
                });
            }
        };
    }

    @Test
    void test_run_task_unable_schedule_due_to_interval(Vertx vertx, VertxTestContext testContext) {
        final Checkpoint checkpoint = testContext.checkpoint(1);
        IntervalTaskExecutor.builder()
                            .vertx(vertx)
                            .trigger(IntervalTrigger.builder().interval(-1).build())
                            .task((jobData, ctx) -> {})
                            .monitor(unableScheduleAsserter(testContext, checkpoint))
                            .build()
                            .start();
    }

    @Test
    void test_run_task_unable_schedule_due_to_initial(Vertx vertx, VertxTestContext testContext) {
        final Checkpoint checkpoint = testContext.checkpoint(1);
        IntervalTaskExecutor.builder()
                            .vertx(vertx)
                            .trigger(IntervalTrigger.builder().initialDelay(-1).build())
                            .task((jobData, ctx) -> {})
                            .monitor(unableScheduleAsserter(testContext, checkpoint))
                            .build()
                            .start();
    }

    @Test
    void test_run_task_after_delay(Vertx vertx, VertxTestContext testContext) {
        final Checkpoint checkpoint = testContext.checkpoint(2);
        final WorkerExecutor worker = vertx.createSharedWorkerExecutor("TEST_PERIODIC", 3);
        final Task task = (jobData, ctx) -> {};
        final TaskExecutorMonitor monitor = new TaskExecutorLogMonitor() {
            @Override
            public void onSchedule(@NonNull TaskResult result) {
                TaskExecutorLogMonitor.super.onSchedule(result);
                testContext.verify(() -> {
                    checkpoint.flag();
                    Assertions.assertNotNull(result.availableAt());
                    Assertions.assertEquals(0, result.tick());
                    Assertions.assertEquals(0, result.round());
                });
            }

            @Override
            public void onCompleted(@NonNull TaskResult result) {
                TaskExecutorLogMonitor.super.onCompleted(result);
                testContext.verify(() -> {
                    checkpoint.flag();
                    Assertions.assertEquals(2, result.round());
                    Assertions.assertTrue(result.isCompleted());
                    Assertions.assertFalse(result.isError());
                    testContext.completeNow();
                });
            }
        };
        IntervalTaskExecutor.builder()
                            .vertx(vertx)
                            .trigger(IntervalTrigger.builder().initialDelay(2).interval(2).repeat(2).build())
                            .task(task)
                            .monitor(monitor)
                            .build()
                            .start(worker);
    }

    @Test
    void test_run_blocking_task_in_the_end(Vertx vertx, VertxTestContext testContext) {
        final Checkpoint checkpoint = testContext.checkpoint(3);
        final WorkerExecutor worker = vertx.createSharedWorkerExecutor("TEST_PERIODIC", 3);
        final Task task = (jobData, ctx) -> {
            TestHelper.sleep(4000);
            checkpoint.flag();
        };
        final TaskExecutorMonitor monitor = new TaskExecutorLogMonitor() {
            @Override
            public void onCompleted(@NonNull TaskResult result) {
                TaskExecutorLogMonitor.super.onCompleted(result);
                testContext.verify(() -> {
                    checkpoint.flag();
                    Assertions.assertEquals(3, result.round());
                    Assertions.assertTrue(result.isCompleted());
                    Assertions.assertFalse(result.isError());
                    testContext.completeNow();
                });
            }
        };
        IntervalTaskExecutor.builder()
                            .vertx(vertx)
                            .trigger(IntervalTrigger.builder().interval(2).repeat(3).build())
                            .task(task)
                            .monitor(monitor)
                            .build()
                            .start(worker);
    }

    @Test
    void test_cancel_task_in_condition(Vertx vertx, VertxTestContext testContext) {
        final Checkpoint checkpoint = testContext.checkpoint(5);
        final Task task = (jobData, ctx) -> {
            checkpoint.flag();
            final long round = ctx.round();
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
        final TaskExecutorMonitor monitor = new TaskExecutorLogMonitor() {

            @Override
            public void onEach(@NonNull TaskResult result) {
                TaskExecutorLogMonitor.super.onEach(result);
                if (result.round() == 2) {
                    Assertions.assertTrue(result.isError());
                    Assertions.assertNotNull(result.error());
                    Assertions.assertTrue(result.error() instanceof RuntimeException);
                }
                if (result.round() == 4) {
                    Assertions.assertTrue(result.isError());
                    Assertions.assertNotNull(result.error());
                    Assertions.assertTrue(result.error() instanceof CarlException);
                }
            }

            @Override
            public void onCompleted(@NonNull TaskResult result) {
                TaskExecutorLogMonitor.super.onCompleted(result);
                Assertions.assertEquals(5, result.round());
                Assertions.assertTrue(result.isCompleted());
                Assertions.assertFalse(result.isError());
            }
        };
        IntervalTaskExecutor.builder()
                            .vertx(vertx)
                            .trigger(IntervalTrigger.builder().interval(1).repeat(10).build())
                            .task(task)
                            .monitor(monitor)
                            .build()
                            .start();
    }

}

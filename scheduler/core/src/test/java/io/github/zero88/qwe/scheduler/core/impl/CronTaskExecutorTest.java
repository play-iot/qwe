package io.github.zero88.qwe.scheduler.core.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.zero88.qwe.TestHelper;
import io.github.zero88.qwe.scheduler.core.TaskExecutorLogMonitor;
import io.github.zero88.qwe.scheduler.core.TaskExecutorMonitor;
import io.github.zero88.qwe.scheduler.core.TaskResult;
import io.github.zero88.qwe.scheduler.core.trigger.CronTrigger;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import lombok.NonNull;

@ExtendWith(VertxExtension.class)
class CronTaskExecutorTest {

    @BeforeAll
    static void setup() {
        TestHelper.setup();
    }

    @Test
    void test_unable_schedule_due_to_initial(Vertx vertx, VertxTestContext testContext) {
        final Checkpoint checkpoint = testContext.checkpoint(2);
        CronTaskExecutor.builder()
                        .vertx(vertx)
                        .trigger(CronTrigger.builder().expression("0/").build())
                        .task((jobData, ctx) -> {})
                        .monitor(TaskExecutorTestHelper.unableScheduleAsserter(testContext, checkpoint))
                        .build()
                        .start();
    }

    @Test
    void test_run_task_by_cron(Vertx vertx, VertxTestContext testContext) {
        final Checkpoint checkpoint = testContext.checkpoint(3);
        final TaskExecutorMonitor monitor = new TaskExecutorLogMonitor() {

            @Override
            public void onSchedule(@NonNull TaskResult result) {
                TaskExecutorLogMonitor.super.onSchedule(result);
                testContext.verify(() -> {
                    checkpoint.flag();
                    if (!result.isReschedule()) {
                        Assertions.assertNotNull(result.availableAt());
                        Assertions.assertEquals(0, result.tick());
                        Assertions.assertEquals(0, result.round());
                    } else {
                        Assertions.assertNotNull(result.availableAt());
                        Assertions.assertTrue(result.isReschedule());
                    }
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
        CronTaskExecutor.builder()
                        .vertx(vertx)
                        .trigger(CronTrigger.builder().expression("0/5 * * ? * * *").build())
                        .task((jobData, ctx) -> {
                            if (ctx.round() == 2) {
                                ctx.forceStopExecution();
                            }
                        })
                        .monitor(monitor)
                        .build()
                        .start();
    }

}

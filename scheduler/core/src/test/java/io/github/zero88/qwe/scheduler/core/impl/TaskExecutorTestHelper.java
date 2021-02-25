package io.github.zero88.qwe.scheduler.core.impl;

import org.junit.jupiter.api.Assertions;

import io.github.zero88.qwe.scheduler.core.TaskExecutorLogMonitor;
import io.github.zero88.qwe.scheduler.core.TaskExecutorMonitor;
import io.github.zero88.qwe.scheduler.core.TaskResult;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

import lombok.NonNull;

class TaskExecutorTestHelper {

    static TaskExecutorMonitor unableScheduleAsserter(VertxTestContext testContext, Checkpoint checkpoint) {
        return new TaskExecutorLogMonitor() {
            @Override
            public void unableSchedule(@NonNull TaskResult result) {
                testContext.verify(() -> {
                    checkpoint.flag();
                    Assertions.assertNotNull(result.unscheduledAt());
                    Assertions.assertNull(result.availableAt());
                    Assertions.assertTrue(result.error() instanceof IllegalArgumentException);
                    testContext.completeNow();
                });
            }
        };
    }

}

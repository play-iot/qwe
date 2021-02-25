package io.github.zero88.qwe.scheduler.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;

public interface TaskExecutorLogMonitor extends TaskExecutorMonitor {

    Logger LOGGER = LoggerFactory.getLogger(TaskExecutorLogMonitor.class);
    TaskExecutorMonitor LOG_MONITOR = new TaskExecutorLogMonitor() {};

    @Override
    default void unableSchedule(@NonNull TaskResult result) {
        LOGGER.error("Unable schedule task at [{}] due to error", result.availableAt(), result.error());
    }

    @Override
    default void onSchedule(@NonNull TaskResult result) {
        LOGGER.debug("TaskExecutor is scheduled at [{}]", result.availableAt());
    }

    @Override
    default void misfire(@NonNull TaskResult result) {
        LOGGER.debug("Misfire tick [{}] at [{}]", result.tick(), result.triggeredAt());
    }

    @Override
    default void onEach(@NonNull TaskResult result) {
        LOGGER.debug("Finish round [{}] - Is Error [{}] | Executed at [{}] - Finished at [{}]", result.round(),
                     result.isError(), result.executedAt(), result.finishedAt());
    }

    @Override
    default void onCompleted(@NonNull TaskResult result) {
        LOGGER.debug("Completed task in round [{}] at [{}]", result.round(), result.completedAt());
    }

}

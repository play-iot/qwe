package io.github.zero88.qwe.scheduler.core;

import java.time.Instant;
import java.util.Objects;

public interface TaskResult {

    /**
     * Only {@code not null} in {@link TaskExecutorMonitor#unableSchedule(TaskResult)}
     *
     * @return unschedule at time
     */
    Instant unscheduledAt();

    /**
     * Only {@code not null} if reschedule {@link TaskExecutorMonitor#unableSchedule(TaskResult)}
     *
     * @return reschedule at time
     * @see #isReschedule()
     */
    Instant rescheduledAt();

    Instant availableAt();

    Instant triggeredAt();

    Instant executedAt();

    Instant finishedAt();

    Instant completedAt();

    long tick();

    long round();

    Throwable error();

    Object data();

    boolean isCompleted();

    default boolean isError() {
        return Objects.nonNull(error());
    }

    default boolean isReschedule() {
        return Objects.nonNull(rescheduledAt()) && round() > 0;
    }

}

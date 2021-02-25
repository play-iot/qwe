package io.github.zero88.qwe.scheduler.core;

import java.time.Instant;
import java.util.Objects;

public interface TaskResult {

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

}

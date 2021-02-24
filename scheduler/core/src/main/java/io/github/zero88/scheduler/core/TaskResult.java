package io.github.zero88.scheduler.core;

import java.util.Objects;

public interface TaskResult {

    long getRound();

    boolean isCompleted();

    Throwable getError();

    Object getData();

    default boolean isError() {
        return Objects.nonNull(getError());
    }

}

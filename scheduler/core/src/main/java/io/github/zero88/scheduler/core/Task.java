package io.github.zero88.scheduler.core;

import lombok.NonNull;

public interface Task {

    default boolean isAsync() {
        return false;
    }

    void execute(@NonNull TaskExecutionContext executionContext);

}

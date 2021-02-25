package io.github.zero88.qwe.scheduler.core;

import lombok.NonNull;

public interface Task {

    default boolean isAsync() {
        return false;
    }

    void execute(@NonNull TaskExecutionContext executionContext);

}

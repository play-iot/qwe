package io.github.zero88.qwe.scheduler.core;

import java.time.Instant;

import lombok.NonNull;

public interface TaskExecutorMonitor {

    void misfire(long tick, Instant now);

    void onEach(@NonNull TaskResult data);

    void onCompleted(@NonNull TaskResult data);

    TaskExecutorMonitor NO_MONITOR = new TaskExecutorNoMonitor() {};


    interface TaskExecutorNoMonitor extends TaskExecutorMonitor {

        @Override
        default void misfire(long tick, Instant now) { }

        @Override
        default void onEach(@NonNull TaskResult data) { }

        @Override
        default void onCompleted(@NonNull TaskResult data) { }

    }

}

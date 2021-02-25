package io.github.zero88.qwe.scheduler.core;

import io.github.zero88.qwe.scheduler.core.trigger.Trigger;

import lombok.NonNull;

public interface TriggerTaskExecutor<T extends Trigger, C extends TaskExecutionContext> extends TaskExecutor<C> {

    @NonNull Task task();

    @NonNull T trigger();

    @NonNull JobData jobData();

    @NonNull TaskExecutorMonitor monitor();

}

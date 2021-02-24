package io.github.zero88.scheduler.core;

import io.vertx.core.Promise;

import lombok.NonNull;

public interface TaskExecutionContext {

    long round();

    @NonNull Promise<Object> promise();

    boolean isForceStop();

    void forceStopExecution();

}

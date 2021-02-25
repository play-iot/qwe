package io.github.zero88.qwe.scheduler.core;

import io.vertx.core.Promise;

import lombok.NonNull;

public interface TaskExecutionContext {

    long round();

    @NonNull Promise<Object> promise();

    boolean isForceStop();

    void forceStopExecution();

}

package io.github.zero88.qwe.scheduler.core;

import io.vertx.core.Promise;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
public final class TaskExecutionContextImpl implements TaskExecutionContext {

    @Accessors(fluent = true)
    private final long round;
    @NonNull
    @Accessors(fluent = true)
    private final Promise<Object> promise;
    private boolean forceStop = false;

    @Override
    public void forceStopExecution() {
        this.forceStop = true;
    }

}

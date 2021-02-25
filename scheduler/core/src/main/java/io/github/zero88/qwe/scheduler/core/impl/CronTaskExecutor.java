package io.github.zero88.qwe.scheduler.core.impl;

import io.github.zero88.qwe.scheduler.core.trigger.CronTrigger;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.WorkerExecutor;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class CronTaskExecutor extends AbstractTaskExecutor<CronTrigger> {

    @Override
    protected @NonNull Future<Long> addTimer(Promise<Long> promise, WorkerExecutor workerExecutor) {
        promise.complete(0L);
        return promise.future();
    }

    @Override
    protected boolean shouldCancel(long round) {
        return false;
    }

}

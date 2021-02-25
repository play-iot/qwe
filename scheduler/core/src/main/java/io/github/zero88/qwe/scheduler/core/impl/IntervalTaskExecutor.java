package io.github.zero88.qwe.scheduler.core.impl;

import io.github.zero88.qwe.scheduler.core.trigger.IntervalTrigger;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.WorkerExecutor;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class IntervalTaskExecutor extends AbstractTaskExecutor<IntervalTrigger> {

    protected @NonNull Future<Long> addTimer(@NonNull Promise<Long> promise, WorkerExecutor workerExecutor) {
        try {
            final long i = trigger().intervalInMilliseconds();
            if (trigger().getInitialDelay() == 0) {
                promise.complete(vertx().setPeriodic(i, timerId -> run(workerExecutor)));
            } else {
                vertx().setTimer(trigger().delayInMilliseconds(),
                                 ignore -> promise.complete(vertx().setPeriodic(i, timerId -> run(workerExecutor))));
            }
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    protected boolean shouldCancel(long round) {
        return trigger().getRepeat() != IntervalTrigger.REPEAT_INDEFINITELY && round >= trigger().getRepeat();
    }

}

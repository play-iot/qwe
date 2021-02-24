package io.github.zero88.scheduler.core;

import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;

import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;

@Builder(builderClassName = "Builder")
public final class PeriodicTaskExecutor implements TaskExecutor {

    @NonNull
    private final Vertx vertx;
    @Default
    private final long repeat = 1;
    @Default
    private final long interval = 1;
    @Default
    private final TimeUnit timeUnit = TimeUnit.SECONDS;
    @Default
    @NonNull
    private final TaskExecutorMonitor monitor = TaskExecutorMonitor.NO_MONITOR;
    @NonNull
    private final Task task;

    private final AtomicLong tick = new AtomicLong(0);
    private final AtomicLong round = new AtomicLong(0);
    private final AtomicBoolean completed = new AtomicBoolean(false);
    private final AtomicBoolean executing = new AtomicBoolean(false);
    private final AtomicReference<Entry<Long, Object>> data = new AtomicReference<>(new SimpleEntry<>(0L, null));
    private final AtomicReference<Entry<Long, Throwable>> error = new AtomicReference<>(new SimpleEntry<>(0L, null));
    private long periodicId;

    @Override
    public void start(WorkerExecutor workerExecutor) {
        this.periodicId = vertx.setPeriodic(TimeUnit.MILLISECONDS.convert(interval, timeUnit), timerId -> {
            long tick = this.tick.incrementAndGet();
            if (isExecuting()) {
                LOGGER.debug("Task is still executing. Skip tick [{}]", tick);
                monitor.misfire(tick, Instant.now());
                return;
            }
            final long round = this.round.incrementAndGet();
            LOGGER.debug("TaskExecutor begins running at round [{}]", round);
            if (workerExecutor != null) {
                workerExecutor.executeBlocking(promise -> onExecute(promise, round),
                                               asyncResult -> onResult(asyncResult, round, timerId));
            } else {
                vertx.executeBlocking(promise -> onExecute(promise, round),
                                      asyncResult -> onResult(asyncResult, round, timerId));
            }
        });
    }

    public void cancel() {
        if (!isCompleted()) {
            LOGGER.debug("TaskExecutor is canceled at round [{}]", currentRound());
            vertx.cancelTimer(periodicId);
            onCompleted();
        }
    }

    public long currentRound() {
        return round.get();
    }

    public boolean isExecuting() {
        return executing.get();
    }

    public boolean isCompleted() {
        return completed.get();
    }

    @Override
    public Object lastData() {
        return Optional.ofNullable(data.get()).map(Entry::getValue).orElse(null);
    }

    @Override
    public Throwable lastError() {
        return Optional.ofNullable(error.get()).map(Entry::getValue).orElse(null);
    }

    private void onExecute(@NonNull Promise<Object> promise, long round) {
        try {
            executing.set(true);
            LOGGER.debug("TaskExecutor executes a registered task at round [{}]", round);
            TaskExecutionContext ctx = new TaskExecutionContextImpl(round, promise);
            task.execute(ctx);
            if (!task.isAsync()) {
                promise.tryComplete();
            }
            if (ctx.isForceStop()) {
                cancel();
            }
        } catch (Exception ex) {
            promise.fail(ex);
        }
    }

    private void onResult(@NonNull AsyncResult<Object> asyncResult, long round, long timerId) {
        LOGGER.debug("TaskExecutor handles a task result at round [{}]", round);
        executing.set(false);
        if (asyncResult.succeeded()) {
            data.accumulateAndGet(new SimpleEntry<>(round, asyncResult.result()),
                                  BinaryOperator.maxBy(Comparator.comparingLong(Entry::getKey)));
            monitor.onEach(TaskResultImpl.builder().round(round).data(asyncResult.result()).build());
        } else {
            onError(round, asyncResult.cause());
        }
        if (round == repeat) {
            vertx.cancelTimer(timerId);
            onCompleted();
        }
    }

    private void onError(long round, Throwable e) {
        LOGGER.debug("TaskExecutor catches a task exception at round [{}]", round, e);
        error.accumulateAndGet(new SimpleEntry<>(round, e),
                               BinaryOperator.maxBy(Comparator.comparingLong(Entry::getKey)));
        monitor.onEach(TaskResultImpl.builder().round(round).error(e).build());
    }

    private void onCompleted() {
        LOGGER.debug("TaskExecutor is completed at round [{}]", currentRound());
        completed.set(true);
        monitor.onCompleted(
            TaskResultImpl.builder().round(currentRound()).completed(true).data(lastData()).error(lastError()).build());
    }

    public static class Builder {

        private Builder periodicId(long periodicId) {
            return this;
        }

    }

}

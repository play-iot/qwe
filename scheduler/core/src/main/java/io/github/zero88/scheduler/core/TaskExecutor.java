package io.github.zero88.scheduler.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.WorkerExecutor;

public interface TaskExecutor {

    Logger LOGGER = LoggerFactory.getLogger(TaskExecutor.class);

    default void start() {
        start(null);
    }

    void start(WorkerExecutor workerExecutor);

    void cancel();

    long currentRound();

    boolean isExecuting();

    boolean isCompleted();

    Object lastData();

    Throwable lastError();

}

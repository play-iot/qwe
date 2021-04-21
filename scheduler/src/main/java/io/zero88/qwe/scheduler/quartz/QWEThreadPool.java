package io.zero88.qwe.scheduler.quartz;

import java.util.Objects;

import org.quartz.spi.ThreadPool;

import io.zero88.qwe.scheduler.SchedulerConfig.WorkerPoolConfig;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

import lombok.NonNull;

public final class QWEThreadPool implements ThreadPool {

    private final WorkerExecutor worker;
    private final WorkerPoolConfig config;

    public QWEThreadPool(@NonNull Vertx vertx, @NonNull WorkerPoolConfig config) {
        this.worker = vertx.createSharedWorkerExecutor(config.getPoolName(), config.getPoolSize(),
                                                       config.getMaxExecuteTime(), config.getMaxExecuteTimeUnit());
        this.config = config;
    }

    @Override
    public boolean runInThread(Runnable runnable) {
        if (Objects.isNull(runnable)) {
            return false;
        }
        worker.executeBlocking(future -> run(runnable, future), null);
        return true;
    }

    @Override
    public int blockForAvailableThreads() { return 1; }

    @Override
    public void initialize() { }

    @Override
    public void shutdown(boolean waitForJobsToComplete) { worker.close(); }

    @Override
    public int getPoolSize() { return this.config.getPoolSize(); }

    @Override
    public void setInstanceId(String schedInstId) { }

    @Override
    public void setInstanceName(String schedName) { }

    private void run(Runnable runnable, Promise<Object> future) {
        runnable.run();
        future.complete();
    }

}

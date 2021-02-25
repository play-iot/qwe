package io.github.zero88.qwe.scheduler.core;

import lombok.NonNull;

/**
 * Represents for monitor that watches lifecycle event in executor
 *
 * @see TaskResult
 */
public interface TaskExecutorMonitor {

    /**
     * Invoke when executor is unable to schedule
     *
     * @param result task result
     * @see TaskResult
     */
    void unableSchedule(@NonNull TaskResult result);

    /**
     * Invoke when executor is available
     *
     * @param result task result
     */
    void onSchedule(@NonNull TaskResult result);

    /**
     * Invoke when misfire
     *
     * @param result task result
     */
    void misfire(@NonNull TaskResult result);

    /**
     * Invoke after each round is finished
     *
     * @param result task result
     */
    void onEach(@NonNull TaskResult result);

    /**
     * Invoke after executor is completed
     *
     * @param result task result
     */
    void onCompleted(@NonNull TaskResult result);

}

package io.github.zero88.qwe.scheduler.core;

import java.time.Instant;

/**
 * Represents for current Task executor state
 */
public interface TaskExecutorState {

    long timerId();

    Instant availableAt();

    long tick();

    long round();

    /**
     * Check whether executor is in {@code pending} state that means is not in a {@code scheduler}
     *
     * @return {@code true} if pending
     */
    boolean pending();

    /**
     * Check whether {@code executor} is in executing state
     *
     * @return {@code true} if in executing
     */
    boolean executing();

    /**
     * Check whether {@code executor} is {@code idle} state that means is in {@code scheduler} but in {@code break-time}
     * between 2 executions
     *
     * @return {@code true} if idle
     */
    default boolean idle() {
        return !executing() && !completed() && !pending();
    }

    /**
     * Check whether {@code executor} is {@code completed} state that means safe to remove out of a {@code scheduler}
     *
     * @return {@code true} if completed
     */
    boolean completed();

    Object lastData();

    Throwable lastError();

}

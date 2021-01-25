package io.github.zero88.qwe.scheduler.solution;

public interface IntervalTrigger extends Trigger {

    /**
     * Default initial delay before firing trigger in first time.
     */
    long DEFAULT_INITIAL_DELAY = 0;
    /**
     * Used to indicate the 'repeat count' of the trigger is indefinite. Or in other words, the trigger should repeat
     * continually until the trigger's ending timestamp.
     */
    long REPEAT_INDEFINITELY = Integer.MAX_VALUE;

    /**
     * Get the initial delay time (in {@code milliseconds}) before firing trigger in first time.
     *
     * @see #DEFAULT_INITIAL_DELAY
     */
    default long getInitialDelay() {
        return DEFAULT_INITIAL_DELAY;
    }

    /**
     * Get the number of times the {@code IntervalTrigger} should repeat, after which it will be automatically deleted.
     *
     * @see #REPEAT_INDEFINITELY
     */
    default long getRepeatCount() {
        return REPEAT_INDEFINITELY;
    }

    /**
     * Get the time interval (in {@code milliseconds}) at which the {@code IntervalTrigger} should repeat.
     */
    long getPeriod();

    /**
     * Get the number of times the {@code IntervalTrigger} has already fired.
     */
    long getTimesTriggered();

}

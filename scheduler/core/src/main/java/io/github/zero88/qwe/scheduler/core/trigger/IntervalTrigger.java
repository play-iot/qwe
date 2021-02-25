package io.github.zero88.qwe.scheduler.core.trigger;

import java.util.concurrent.TimeUnit;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public final class IntervalTrigger implements Trigger {

    /**
     * Used to indicate the 'repeat count' of the trigger is indefinite. Or in other words, the trigger should repeat
     * continually until the trigger's ending timestamp.
     */
    public static final long REPEAT_INDEFINITELY = -1;

    /**
     * Delay time unit
     *
     * @apiNote Default is {@code SECONDS}
     */
    @Default
    private final TimeUnit initialDelayTimeUnit = TimeUnit.SECONDS;
    /**
     * Get the initial delay time (in {@link #getInitialDelayTimeUnit()}) before firing trigger in first time.
     *
     * @apiNote Default is {@code 0}
     */
    @Default
    private final long initialDelay = 0;
    /**
     * Get the number of times the {@code IntervalTrigger} should repeat, after which it will be automatically deleted.
     *
     * @see #REPEAT_INDEFINITELY
     */
    @Default
    private final long repeat = REPEAT_INDEFINITELY;
    /**
     * Interval time unit
     *
     * @apiNote Default is {@code SECONDS}
     */
    @Default
    private final TimeUnit intervalTimeUnit = TimeUnit.SECONDS;
    /**
     * Get the time interval (in {@link #getIntervalTimeUnit()}) at which the {@code IntervalTrigger} should repeat.
     */
    private final long interval;

    public long intervalInMilliseconds() {
        if (interval < 0) {
            throw new IllegalArgumentException("Interval must be greater than 0");
        }
        return TimeUnit.MILLISECONDS.convert(getInterval(), getIntervalTimeUnit());
    }

    public long delayInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(getInitialDelay(), getInitialDelayTimeUnit());
    }

}

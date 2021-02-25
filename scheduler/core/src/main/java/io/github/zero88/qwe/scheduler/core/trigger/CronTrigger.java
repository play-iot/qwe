package io.github.zero88.qwe.scheduler.core.trigger;

import java.util.TimeZone;

import io.github.zero88.qwe.scheduler.core.Task;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;

/**
 * The public interface for inspecting settings specific to a CronTrigger, which is used to fire a <code>{@link
 * Task}</code> at given moments in time, defined with Unix 'cron-like' schedule definitions.
 */
@Getter
@Builder
public final class CronTrigger implements Trigger {

    /**
     * Returns the cron expression
     *
     * @see CronExpression
     */
    @NonNull
    private final CronExpression expression;

    /**
     * Returns the time zone for which the {@code cronExpression} of this {@code CronTrigger} will be resolved.
     */
    @Default
    private final TimeZone timeZone = TimeZone.getDefault();

}

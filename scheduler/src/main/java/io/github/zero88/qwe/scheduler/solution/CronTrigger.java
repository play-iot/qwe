package io.github.zero88.qwe.scheduler.solution;

import java.util.Date;
import java.util.TimeZone;

/**
 * The public interface for inspecting settings specific to a CronTrigger, which is used to fire a <code>{@link
 * org.quartz.Job}</code> at given moments in time, defined with Unix 'cron-like' schedule definitions.
 */
public interface CronTrigger extends Trigger {

    /**
     * Returns the cron expression
     *
     * @return cronExpression
     * @see CronExpression
     */
    CronExpression getCronExpression();

    /**
     * Returns the time zone for which the {@code cronExpression} of this {@code CronTrigger} will be resolved.
     *
     * @return TimeZone
     */
    TimeZone getTimeZone();

    @Override
    default Date getEndTime() { return null; }

    @Override
    default Date getFinalFireTime() { return null; }

}

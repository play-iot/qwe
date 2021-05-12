package io.zero88.qwe.scheduler.quartz;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import io.zero88.qwe.HasSharedData;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.scheduler.job.QWEJob;
import io.vertx.core.shareddata.SharedData;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents for QWE Job factory to create new QWE job instance
 *
 * @see JobFactory
 * @see SharedData
 * @see QWEJob
 */
@RequiredArgsConstructor
public final class QWEJobFactory extends SimpleJobFactory implements JobFactory, HasSharedData {

    @Getter
    @Accessors(fluent = true)
    private final SharedDataLocalProxy sharedData;

    @Override
    @SuppressWarnings("rawtypes")
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        final Job job = super.newJob(bundle, scheduler);
        final Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
        if (QWEJob.class.isAssignableFrom(jobClass)) {
            return ((QWEJob) job).init(sharedData);
        }
        return job;
    }

}

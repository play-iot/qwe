package io.github.zero88.qwe.scheduler.job;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import io.github.zero88.qwe.component.HasSharedData;
import io.github.zero88.qwe.component.SharedDataLocalProxy;

import io.github.zero88.qwe.scheduler.SchedulerConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public final class VertxJobFactory extends SimpleJobFactory implements JobFactory, HasSharedData {

    @Getter
    @Accessors(fluent = true)
    private final SharedDataLocalProxy sharedData;
    private final SchedulerConfig config;

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        Job job = super.newJob(bundle, scheduler);
        final Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
        if (VertxJob.class.isAssignableFrom(jobClass)) {
            return ((VertxJob) job).init(sharedData, config);
        }
        return job;
    }

}

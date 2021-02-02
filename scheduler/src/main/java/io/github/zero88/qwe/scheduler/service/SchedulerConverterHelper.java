package io.github.zero88.qwe.scheduler.service;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import io.github.zero88.qwe.scheduler.converter.CronTriggerConverter;
import io.github.zero88.qwe.scheduler.converter.PeriodicTriggerConverter;
import io.github.zero88.qwe.scheduler.converter.QWEJobConverter;
import io.github.zero88.qwe.scheduler.model.job.QWEJobModel;
import io.github.zero88.qwe.scheduler.model.trigger.CronTriggerModel;
import io.github.zero88.qwe.scheduler.model.trigger.PeriodicTriggerModel;
import io.github.zero88.qwe.scheduler.model.trigger.QWETriggerModel;
import io.github.zero88.qwe.scheduler.model.trigger.TriggerType;

import lombok.NonNull;

public interface SchedulerConverterHelper {

    static SchedulerConverterHelper create() {
        return new SchedulerConverterHelper() {};
    }

    default JobDetail convertJob(@NonNull QWEJobModel jobModel) {
        return new QWEJobConverter().to(jobModel);
    }

    default Trigger convertTrigger(@NonNull QWETriggerModel triggerModel) {
        if (triggerModel.type() == TriggerType.CRON) {
            return new CronTriggerConverter().to((CronTriggerModel) triggerModel);
        }
        if (triggerModel.type() == TriggerType.PERIODIC) {
            return new PeriodicTriggerConverter().to((PeriodicTriggerModel) triggerModel);
        }
        throw new IllegalArgumentException("Unknown trigger type");
    }

}

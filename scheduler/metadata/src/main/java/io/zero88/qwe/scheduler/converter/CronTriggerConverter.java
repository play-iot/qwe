package io.zero88.qwe.scheduler.converter;

import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import io.zero88.qwe.scheduler.model.trigger.CronTriggerModel;

public class CronTriggerConverter implements QuartzConverter<CronTriggerModel, Trigger> {

    @Override
    public Trigger to(CronTriggerModel trigger) {
        return TriggerBuilder.newTrigger()
                             .withIdentity(trigger.getKey())
                             .withSchedule(CronScheduleBuilder.cronSchedule(trigger.getExpression())
                                                              .inTimeZone(trigger.getTimezone()))
                             .build();
    }

}

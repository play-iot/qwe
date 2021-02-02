package io.github.zero88.qwe.scheduler.converter;

import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import io.github.zero88.qwe.scheduler.model.trigger.PeriodicTriggerModel;

public class PeriodicTriggerConverter implements QuartzConverter<PeriodicTriggerModel, Trigger> {

    @Override
    public Trigger to(PeriodicTriggerModel trigger) {
        return TriggerBuilder.newTrigger()
                             .withIdentity(trigger.getKey())
                             .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                                                .withIntervalInSeconds(trigger.getIntervalInSeconds())
                                                                .withRepeatCount(trigger.getRepeat()))
                             .build();
    }

}

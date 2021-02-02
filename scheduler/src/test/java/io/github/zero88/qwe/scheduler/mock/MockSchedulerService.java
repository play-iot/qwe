package io.github.zero88.qwe.scheduler.mock;

import org.quartz.Scheduler;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.scheduler.service.SchedulerConverterHelper;
import io.github.zero88.qwe.scheduler.service.SchedulerRegisterService;

import lombok.NonNull;

public class MockSchedulerService extends SchedulerRegisterService {

    public MockSchedulerService(@NonNull Scheduler scheduler, @NonNull SharedDataLocalProxy sharedData,
                                @NonNull SchedulerConverterHelper converterHelper) {
        super(scheduler, sharedData, converterHelper);
    }

}

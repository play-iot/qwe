package io.github.zero88.qwe.scheduler.mock;

import org.quartz.Scheduler;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.scheduler.service.SchedulerService;

import lombok.NonNull;

public class MockSchedulerService extends SchedulerService {

    public MockSchedulerService(@NonNull Scheduler scheduler, @NonNull SharedDataLocalProxy sharedData) {
        super(scheduler, sharedData);
    }

}

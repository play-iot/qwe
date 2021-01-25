package io.github.zero88.qwe.scheduler.job;

import org.quartz.Job;
import org.quartz.JobDataMap;

import io.github.zero88.qwe.component.HasSharedData;
import io.github.zero88.qwe.component.SharedDataLocalProxy;

import io.github.zero88.qwe.scheduler.SchedulerConfig;

public interface VertxJob<J extends JobModel> extends Job, HasSharedData {

    SchedulerConfig config();

    Job init(SharedDataLocalProxy sharedData, SchedulerConfig config);

    @SuppressWarnings("unchecked")
    default J getJobModel(JobDataMap jobDataMap) {
        return (J) jobDataMap.get(JobModel.JOB_DATA_KEY);
    }

}

package io.github.zero88.qwe.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import io.github.zero88.qwe.component.HasSharedData;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.scheduler.model.job.QWEJobModel;

import lombok.NonNull;

/**
 * Declares a particular QWE Job is corresponding to QWE Job model that will be perform in scheduler
 *
 * @param <J> Type of job model
 * @see Job
 * @see QWEJob
 */
public interface QWEJob<J extends QWEJobModel> extends Job, HasSharedData {

    /**
     * Defines job data key to retrieve job model in job execution time
     */
    String JOB_DATA_KEY = "jobModel";
    String MONITOR_ADDRESS_KEY = "SCHEDULER_MONITOR_ADDRESS";

    /**
     * Init job context in factory
     *
     * @param sharedData shared data
     * @return QWE job
     */
    @NonNull Job init(@NonNull SharedDataLocalProxy sharedData);

    @SuppressWarnings("unchecked")
    default J queryJobModel(@NonNull JobExecutionContext executionContext) {
        return (J) executionContext.getMergedJobDataMap().get(JOB_DATA_KEY);
    }

    default String monitorAddress() {
        return sharedData().getData(MONITOR_ADDRESS_KEY);
    }

}

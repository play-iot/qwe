package io.github.zero88.qwe.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import io.github.zero88.qwe.component.HasSharedData;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.scheduler.SchedulerConfig;
import io.github.zero88.qwe.scheduler.model.job.QWEJobModel;

import lombok.NonNull;

/**
 * Declares a particular QWE Job is corresponding to QWE Job model that will be perform in scheduler
 *
 * @param <J> Type of job model
 * @see Job
 * @see QWEJob
 */
@SuppressWarnings("rawtypes")
public interface QWEJob<J extends QWEJobModel> extends Job, HasSharedData {

    /**
     * Init job context in factory
     *
     * @param sharedData shared data
     * @param config     scheduler config
     * @return QWE job
     */
    @NonNull QWEJob init(@NonNull SharedDataLocalProxy sharedData, @NonNull SchedulerConfig config);

    @SuppressWarnings("unchecked")
    default J queryJobModel(@NonNull JobExecutionContext executionContext) {
        return (J) executionContext.getMergedJobDataMap().get(QWEJobModel.JOB_DATA_KEY);
    }

}

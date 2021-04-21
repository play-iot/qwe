package io.zero88.qwe.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import io.zero88.qwe.component.HasSharedData;
import io.zero88.qwe.component.SharedDataLocalProxy;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.EventbusClient;
import io.zero88.qwe.event.Status;
import io.zero88.qwe.scheduler.model.JobResult;
import io.zero88.qwe.scheduler.model.job.QWEJobModel;
import io.vertx.core.json.JsonObject;

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

    default void publishToMonitor(@NonNull JobExecutionContext context, @NonNull JobKey jobKey, @NonNull Status status,
                                  JsonObject result, ErrorMessage error) {
        final JobResult monitor = JobResult.builder()
                                           .status(status)
                                           .error(error)
                                           .result(result)
                                           .jobKey(jobKey.toString())
                                           .triggerKey(context.getTrigger().getKey().toString())
                                           .fireId(context.getFireInstanceId())
                                           .fireTime(context.getFireTime())
                                           .build();
        EventbusClient.create(sharedData())
                      .publish(sharedData().getData(MONITOR_ADDRESS_KEY),
                               EventMessage.success(EventAction.MONITOR, new JsonObject().put("result", monitor)));
    }

}

package io.github.zero88.qwe.scheduler.model.job;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.utils.Key;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.scheduler.job.QWEJob;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Represents for QWE job model that keeps every information to able to execute job
 *
 * @param <T> Type of QWE job
 * @see QWEJob
 */
@SuppressWarnings("rawtypes")
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes( {
    @JsonSubTypes.Type(value = EventbusJobModel.class, name = "EVENTBUS_JOB")
})
public interface QWEJobModel<T extends QWEJob> extends JsonData {

    /**
     * Defines job data key to retrieve job model in job execution time
     */
    String JOB_DATA_KEY = "jobModel";

    static JobKey createKey(String group, String name) {
        return new JobKey(Strings.isBlank(name) ? Key.createUniqueName(group) : name, group);
    }

    @JsonUnwrapped
    JobKey getKey();

    @JsonUnwrapped
    @JsonProperty(value = "type", required = true)
    JobType type();

    Class<T> implementation();

    @JsonProperty(value = "forwardIfFailure")
    boolean forwardIfFailure();

    String toString();

    default JobDetail toJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_DATA_KEY, this);
        return JobBuilder.newJob(implementation()).withIdentity(getKey()).setJobData(jobDataMap).build();
    }

}

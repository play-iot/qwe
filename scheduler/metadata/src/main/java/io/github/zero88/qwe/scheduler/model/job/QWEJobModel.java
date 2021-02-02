package io.github.zero88.qwe.scheduler.model.job;

import org.quartz.JobKey;
import org.quartz.utils.Key;

import io.github.zero88.qwe.scheduler.model.SchedulerModel;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Represents for QWE job model that keeps every information to able to execute job
 */
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes( {
    @JsonSubTypes.Type(value = EventbusJobModel.class, name = "EVENTBUS_JOB")
})
public interface QWEJobModel extends SchedulerModel {

    static JobKey createKey(String group, String name) {
        return new JobKey(Strings.isBlank(name) ? Key.createUniqueName(group) : name, group);
    }

    @JsonUnwrapped
    JobKey getKey();

    @JsonUnwrapped
    @JsonProperty(value = "type", required = true)
    JobType type();

    @JsonProperty(value = "forwardIfFailure")
    boolean forwardIfFailure();

    String toString();

}

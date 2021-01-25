package io.github.zero88.qwe.scheduler.service;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.scheduler.job.JobModel;
import io.github.zero88.qwe.scheduler.trigger.TriggerModel;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SchedulerRequestData implements JsonData {

    static final String JOB_KEY = "job";
    static final String TRIGGER_KEY = "trigger";
    @Include
    @JsonProperty(JOB_KEY)
    private final JobModel job;
    @Include
    @JsonProperty(TRIGGER_KEY)
    private final TriggerModel trigger;

    @JsonCreator
    public static SchedulerRequestData create(@JsonProperty(JOB_KEY) JobModel job,
                                              @JsonProperty(TRIGGER_KEY) TriggerModel trigger) {
        return new SchedulerRequestData(job, trigger);
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject().put(JOB_KEY, job.toJson()).put(TRIGGER_KEY, trigger.toJson());
    }

}

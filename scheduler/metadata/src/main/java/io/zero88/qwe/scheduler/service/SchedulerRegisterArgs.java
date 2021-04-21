package io.zero88.qwe.scheduler.service;

import java.util.Objects;

import org.quartz.JobKey;
import org.quartz.TriggerKey;

import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.scheduler.model.job.QWEJobModel;
import io.zero88.qwe.scheduler.model.trigger.QWETriggerModel;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder
@FieldNameConstants
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SchedulerRegisterArgs implements JsonData {

    private final String jobKey;
    private final QWEJobModel job;

    private final String triggerKey;
    private final QWETriggerModel trigger;

    public static SchedulerRegisterArgs parse(@NonNull RequestData reqData) {
        return JsonData.from(reqData.body(), SchedulerRegisterArgs.class);
    }

    public static String createKey(@NonNull String group, @NonNull String name) {
        return group + "." + name;
    }

    public JobKey jobKey() {
        String[] splitter = parse(jobKey);
        if (Objects.nonNull(splitter)) {
            return JobKey.jobKey(splitter[1], splitter[0]);
        }
        if (Objects.nonNull(job)) {
            return job.getKey();
        }
        return null;
    }

    public TriggerKey triggerKey() {
        String[] splitter = parse(triggerKey);
        if (Objects.nonNull(splitter)) {
            return TriggerKey.triggerKey(splitter[1], splitter[0]);
        }
        if (Objects.nonNull(trigger)) {
            return trigger.getKey();
        }
        return null;
    }

    public JobKey requiredJobKey() {
        return Objects.requireNonNull(jobKey(), "Job key is required");
    }

    public TriggerKey requiredTriggerKey() {
        return Objects.requireNonNull(triggerKey(), "Trigger key is required");
    }

    public QWEJobModel requiredJob() {
        return Objects.requireNonNull(getJob(), "Job is required");
    }

    public QWETriggerModel requiredTrigger() {
        return Objects.requireNonNull(getTrigger(), "Trigger is required");
    }

    private String[] parse(String key) {
        if (Strings.isBlank(key)) {
            return null;
        }
        final String[] split = key.split("\\.", 2);
        if (split.length == 1) {
            return new String[] {null, split[0]};
        }
        return split;
    }

}

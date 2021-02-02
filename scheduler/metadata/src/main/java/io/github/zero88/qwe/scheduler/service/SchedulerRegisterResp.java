package io.github.zero88.qwe.scheduler.service;

import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import org.quartz.JobKey;
import org.quartz.TriggerKey;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.utils.JsonUtils;
import io.vertx.core.json.JsonObject;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@FieldNameConstants
public final class SchedulerRegisterResp implements JsonData {

    private final String jobKey;
    private final String triggerKey;
    private final JsonObject firstFireTime;
    private final JsonObject nextFireTime;
    private final JsonObject prevFireTime;


    public static class SchedulerRegisterRespBuilder {

        public SchedulerRegisterRespBuilder jobKey(@NonNull String jobKey) {
            this.jobKey = jobKey;
            return this;
        }

        public SchedulerRegisterRespBuilder jobKey(@NonNull JobKey jobKey) {
            this.jobKey = jobKey.toString();
            return this;
        }

        public SchedulerRegisterRespBuilder triggerKey(@NonNull String triggerKey) {
            this.triggerKey = triggerKey;
            return this;
        }

        public SchedulerRegisterRespBuilder triggerKey(@NonNull TriggerKey triggerKey) {
            this.triggerKey = triggerKey.toString();
            return this;
        }

        public SchedulerRegisterRespBuilder firstFireTime(JsonObject firstFireTime) {
            this.firstFireTime = firstFireTime;
            return this;
        }

        public SchedulerRegisterRespBuilder firstFireTime(Date firstFireTime, TimeZone tz) {
            this.firstFireTime = computeTime(firstFireTime, tz);
            return this;
        }

        public SchedulerRegisterRespBuilder nextFireTime(JsonObject nextFireTime) {
            this.nextFireTime = nextFireTime;
            return this;
        }

        public SchedulerRegisterRespBuilder nextFireTime(Date nextFireTime, TimeZone tz) {
            this.nextFireTime = computeTime(nextFireTime, tz);
            return this;
        }

        public SchedulerRegisterRespBuilder prevFireTime(JsonObject prevFireTime) {
            this.prevFireTime = prevFireTime;
            return this;
        }

        public SchedulerRegisterRespBuilder prevFireTime(Date prevFireTime, TimeZone tz) {
            this.prevFireTime = computeTime(prevFireTime, tz);
            return this;
        }

        private JsonObject computeTime(Date fireTime, TimeZone tz) {
            if (Objects.isNull(fireTime)) {
                return null;
            }
            return JsonUtils.formatDate(fireTime, tz);
        }

    }

}

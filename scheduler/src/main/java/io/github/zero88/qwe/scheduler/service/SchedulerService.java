package io.github.zero88.qwe.scheduler.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.utils.Key;

import io.github.zero88.qwe.component.HasSharedData;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventContractor;
import io.github.zero88.qwe.event.EventContractor.Param;
import io.github.zero88.qwe.event.EventListener;
import io.github.zero88.qwe.exceptions.AlreadyExistException;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.ErrorCode;
import io.github.zero88.qwe.scheduler.model.job.QWEJobModel;
import io.github.zero88.qwe.scheduler.model.trigger.TriggerModel;
import io.github.zero88.qwe.scheduler.service.SchedulerArgs.Fields;
import io.github.zero88.qwe.utils.JsonUtils;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.json.JsonObject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SchedulerService implements EventListener, HasSharedData {

    public static <T extends SchedulerService> T create(@NonNull Scheduler scheduler,
                                                        @NonNull SharedDataLocalProxy sharedData,
                                                        @NonNull Class<T> clazz) {
        Map<Class, Object> inputs = new LinkedHashMap<>();
        inputs.put(Scheduler.class, scheduler);
        inputs.put(SharedDataLocalProxy.class, sharedData);
        return ReflectionClass.createObject(clazz, inputs);
    }

    @NonNull
    private final Scheduler scheduler;
    @NonNull
    private final SharedDataLocalProxy sharedData;

    @Override
    public @NonNull Collection<EventAction> getAvailableEvents() {
        return Arrays.asList(EventAction.CREATE, EventAction.REMOVE, EventAction.GET_ONE);
    }

    @EventContractor(action = "GET_ONE")
    public JsonObject get(@Param(Fields.job) QWEJobModel jobModel, @Param(Fields.trigger) TriggerModel triggerModel) {
        try {
            final JobDetail jobDetail = jobModel.toJobDetail();
            final Trigger trigger = triggerModel.toTrigger();
            return scheduler.getTriggersOfJob(jobDetail.getKey())
                            .stream()
                            .filter(tr -> tr.getKey().equals(trigger.getKey()))
                            .map(tr -> new JsonObject().put("trigger", keyToJson(tr.getKey()))
                                                       .put("job", keyToJson(jobDetail.getKey()))
                                                       .put("next_fire_time", computeTime(tr.getNextFireTime(), tr))
                                                       .put("prev_fire_time",
                                                            computeTime(tr.getPreviousFireTime(), tr)))
                            .findFirst()
                            .orElse(new JsonObject());
        } catch (SchedulerException e) {
            throw new CarlException(ErrorCode.SERVICE_ERROR, "Cannot get trigger and job in scheduler", e);
        }
    }

    @EventContractor(action = "CREATE")
    public JsonObject create(@Param(Fields.job) QWEJobModel jobModel,
                             @Param(Fields.trigger) TriggerModel triggerModel) {
        try {
            final JobDetail jobDetail = jobModel.toJobDetail();
            final Trigger trigger = triggerModel.toTrigger();
            log.info("Scheduler register | Job: {} | Trigger: {}", jobModel.toJson(), triggerModel.toJson());
            final TriggerKey key = trigger.getKey();
            if (scheduler.checkExists(key)) {
                final JobKey jobKey = scheduler.getTrigger(key).getJobKey();
                throw new AlreadyExistException("Trigger " + key + " is already assigned to another job " + jobKey);
            }
            JsonObject firstFire;
            if (scheduler.checkExists(jobDetail.getKey())) {
                final Trigger refTrigger = trigger.getTriggerBuilder()
                                                  .forJob(jobDetail)
                                                  .usingJobData(jobDetail.getJobDataMap())
                                                  .build();
                firstFire = computeTime(scheduler.scheduleJob(refTrigger), trigger);
            } else {
                firstFire = computeTime(scheduler.scheduleJob(jobDetail, trigger), trigger);
            }
            return new JsonObject().put("trigger", keyToJson(key))
                                   .put("job", keyToJson(jobDetail.getKey()))
                                   .put("first_fire_time", firstFire);
        } catch (SchedulerException e) {
            throw new CarlException(
                e instanceof ObjectAlreadyExistsException ? ErrorCode.ALREADY_EXIST : ErrorCode.SERVICE_ERROR,
                "Cannot add trigger and job in scheduler", e);
        }
    }

    @EventContractor(action = "REMOVE")
    public JsonObject remove(@Param(Fields.job) JsonObject jobKey) {
        final JobKey key = JobKey.jobKey(jobKey.getString("name"), jobKey.getString("group", null));
        try {
            return new JsonObject().put("unschedule", scheduler.deleteJob(key));
        } catch (SchedulerException e) {
            throw new CarlException(ErrorCode.SERVICE_ERROR, "Cannot remove job id " + key.toString(), e);
        }
    }

    private JsonObject keyToJson(@NonNull Key key) {
        return new JsonObject().put("group", key.getGroup()).put("name", key.getName());
    }

    private JsonObject computeTime(Date fireTime, Trigger trigger) {
        if (Objects.isNull(fireTime)) {
            return null;
        }
        if (trigger instanceof CronTrigger) {
            final TimeZone timeZone = ((CronTrigger) trigger).getTimeZone();
            return JsonUtils.formatDate(fireTime, timeZone);
        }
        return JsonUtils.formatDate(fireTime, null);
    }

}

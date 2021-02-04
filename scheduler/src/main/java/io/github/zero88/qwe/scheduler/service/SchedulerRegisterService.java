package io.github.zero88.qwe.scheduler.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import io.github.zero88.qwe.component.HasSharedData;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.dto.msg.RequestData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventContractor;
import io.github.zero88.qwe.event.EventListener;
import io.github.zero88.qwe.exceptions.AlreadyExistException;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.ErrorCode;
import io.github.zero88.qwe.exceptions.NotFoundException;
import io.github.zero88.qwe.scheduler.model.job.QWEJobModel;
import io.github.zero88.qwe.scheduler.model.trigger.QWETriggerModel;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.json.JsonArray;
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
public class SchedulerRegisterService implements EventListener, HasSharedData {

    public static <T extends SchedulerRegisterService> T create(@NonNull Scheduler scheduler,
                                                                @NonNull SharedDataLocalProxy sharedData,
                                                                @NonNull SchedulerConverterHelper helper,
                                                                @NonNull Class<T> clazz) {
        Map<Class, Object> inputs = new LinkedHashMap<>();
        inputs.put(Scheduler.class, scheduler);
        inputs.put(SharedDataLocalProxy.class, sharedData);
        inputs.put(SchedulerConverterHelper.class, helper);
        return ReflectionClass.createObject(clazz, inputs);
    }

    @NonNull
    private final Scheduler scheduler;
    @NonNull
    private final SharedDataLocalProxy sharedData;
    @NonNull
    private final SchedulerConverterHelper converterHelper;

    @Override
    public @NonNull Collection<EventAction> getAvailableEvents() {
        return Arrays.asList(EventAction.CREATE, EventAction.REMOVE, EventAction.GET_ONE, EventAction.GET_LIST);
    }

    @EventContractor(action = "GET_LIST")
    public JsonObject list(@NonNull RequestData requestData) {
        final SchedulerRegisterArgs args = SchedulerRegisterArgs.parse(requestData);
        final JobKey jobKey = args.requiredJobKey();
        try {
            return scheduler.getTriggersOfJob(jobKey)
                            .stream()
                            .map(tr -> {
                                final TimeZone tz = tr instanceof CronTrigger ? ((CronTrigger) tr).getTimeZone() : null;
                                return SchedulerRegisterResp.builder()
                                                            .triggerKey(tr.getKey())
                                                            .nextFireTime(tr.getNextFireTime(), tz)
                                                            .prevFireTime(tr.getPreviousFireTime(), tz)
                                                            .build();
                            })
                            .map(JsonData::toJson)
                            .collect(JsonObject::new,
                                     (r, json) -> r.getJsonArray(jobKey.toString(), new JsonArray()).add(json),
                                     JsonObject::mergeIn);
        } catch (SchedulerException e) {
            throw new CarlException(ErrorCode.SERVICE_ERROR, "Cannot get trigger and job in scheduler", e);
        }
    }

    @EventContractor(action = "GET_ONE", returnType = SchedulerRegisterResp.class)
    public SchedulerRegisterResp get(@NonNull RequestData requestData) {
        final SchedulerRegisterArgs args = SchedulerRegisterArgs.parse(requestData);
        final JobKey jobKey = args.requiredJobKey();
        final TriggerKey triggerKey = args.requiredTriggerKey();
        try {
            return scheduler.getTriggersOfJob(jobKey)
                            .stream()
                            .filter(tr -> tr.getKey().equals(triggerKey))
                            .map(tr -> {
                                final TimeZone tz = tr instanceof CronTrigger ? ((CronTrigger) tr).getTimeZone() : null;
                                return SchedulerRegisterResp.builder()
                                                            .triggerKey(tr.getKey())
                                                            .jobKey(jobKey)
                                                            .nextFireTime(tr.getNextFireTime(), tz)
                                                            .prevFireTime(tr.getPreviousFireTime(), tz)
                                                            .build();
                            })
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(
                                "Not found trigger '" + triggerKey + "' that associated to job '" + jobKey + "'"));
        } catch (SchedulerException e) {
            throw new CarlException(ErrorCode.SERVICE_ERROR, "Cannot get trigger and job in scheduler", e);
        }
    }

    @EventContractor(action = "CREATE", returnType = SchedulerRegisterResp.class)
    public SchedulerRegisterResp create(@NonNull RequestData requestData) {
        final SchedulerRegisterArgs args = SchedulerRegisterArgs.parse(requestData);
        final QWEJobModel jobModel = args.requiredJob();
        final QWETriggerModel triggerModel = args.requiredTrigger();
        try {
            log.info("Scheduler register | Job: {} | Trigger: {}", jobModel.toJson(), triggerModel.toJson());
            final JobDetail jobDetail = converterHelper.convertJob(jobModel);
            final Trigger trigger = converterHelper.convertTrigger(triggerModel);
            final TriggerKey triggerKey = trigger.getKey();
            if (scheduler.checkExists(triggerKey)) {
                final JobKey jobKey = scheduler.getTrigger(triggerKey).getJobKey();
                throw new AlreadyExistException(
                    "Trigger '" + triggerKey + "' is already assigned to another job '" + jobKey + "'");
            }
            Date firstFire;
            TimeZone tz;
            if (scheduler.checkExists(jobDetail.getKey())) {
                final Trigger refTrigger = trigger.getTriggerBuilder()
                                                  .forJob(jobDetail)
                                                  .usingJobData(jobDetail.getJobDataMap())
                                                  .build();
                firstFire = scheduler.scheduleJob(refTrigger);
                tz = refTrigger instanceof CronTrigger ? ((CronTrigger) refTrigger).getTimeZone() : null;
            } else {
                firstFire = scheduler.scheduleJob(jobDetail, trigger);
                tz = trigger instanceof CronTrigger ? ((CronTrigger) trigger).getTimeZone() : null;
            }
            return SchedulerRegisterResp.builder()
                                        .triggerKey(triggerKey)
                                        .jobKey(jobDetail.getKey())
                                        .firstFireTime(firstFire, tz)
                                        .build();
        } catch (SchedulerException e) {
            throw new CarlException(
                e instanceof ObjectAlreadyExistsException ? ErrorCode.ALREADY_EXIST : ErrorCode.SERVICE_ERROR,
                "Cannot add trigger and job in scheduler", e);
        }
    }

    @EventContractor(action = "REMOVE", returnType = SchedulerRegisterResp.class)
    public SchedulerRegisterResp remove(@NonNull RequestData requestData) {
        final SchedulerRegisterArgs args = SchedulerRegisterArgs.parse(requestData);
        final JobKey key = args.requiredJobKey();
        try {
            return SchedulerRegisterResp.builder().removed(scheduler.deleteJob(key)).build();
        } catch (SchedulerException e) {
            throw new CarlException(ErrorCode.SERVICE_ERROR, "Cannot remove job id '" + key.toString() + "'", e);
        }
    }

}

package io.zero88.qwe.scheduler.service;

import java.util.Collection;
import java.util.Collections;

import io.zero88.qwe.component.HasSharedData;
import io.zero88.qwe.component.SharedDataLocalProxy;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventContractor;
import io.zero88.qwe.event.EventContractor.Param;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.event.Status;
import io.zero88.qwe.scheduler.model.JobResult;
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
public class SchedulerMonitorService implements EventListener, HasSharedData {

    public static <T extends SchedulerMonitorService> T create(@NonNull SharedDataLocalProxy sharedData,
                                                               @NonNull Class<T> clazz) {
        return ReflectionClass.createObject(clazz, Collections.singletonMap(SharedDataLocalProxy.class, sharedData));
    }

    @NonNull
    private final SharedDataLocalProxy sharedData;

    @Override
    public @NonNull Collection<EventAction> getAvailableEvents() {
        return Collections.singleton(EventAction.MONITOR);
    }

    @EventContractor(action = "MONITOR")
    public JsonObject monitor(@Param("result") JobResult result) {
        if (result.getStatus() == Status.FAILED) {
            log.warn("Schedule Job::{}", result.toJson());
        } else {
            log.info("Schedule Job::{}", result.toJson());
        }
        return new JsonObject();
    }

}

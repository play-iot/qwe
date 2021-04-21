package io.zero88.qwe.scheduler.converter;

import io.zero88.qwe.scheduler.model.SchedulerModel;

import lombok.NonNull;

public interface QuartzConverter<T extends SchedulerModel, Q> {

    Q to(@NonNull T model);

}

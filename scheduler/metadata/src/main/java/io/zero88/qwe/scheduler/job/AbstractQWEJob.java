package io.zero88.qwe.scheduler.job;

import org.quartz.Job;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.scheduler.model.job.QWEJobModel;

import lombok.Getter;
import lombok.experimental.Accessors;

public abstract class AbstractQWEJob<J extends QWEJobModel> implements QWEJob<J> {

    @Getter
    @Accessors(fluent = true)
    private SharedDataLocalProxy sharedData;

    @Override
    public Job init(SharedDataLocalProxy sharedData) {
        this.sharedData = sharedData;
        return this;
    }

}

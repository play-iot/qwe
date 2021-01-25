package io.github.zero88.qwe.scheduler.solution.impl;

import java.util.List;

import io.github.zero88.qwe.scheduler.solution.JobDetail;
import io.github.zero88.qwe.scheduler.solution.JobKey;
import io.github.zero88.qwe.scheduler.solution.StoreScheduler;
import io.github.zero88.qwe.scheduler.solution.Trigger;
import io.github.zero88.qwe.scheduler.solution.TriggerKey;

public class RamStoreScheduler implements StoreScheduler {

    @Override
    public JobKey add(Trigger trigger, JobDetail definition) {
        return null;
    }

    @Override
    public TriggerKey remove(Trigger trigger) {
        return null;
    }

    @Override
    public JobKey remove(JobDetail trigger) {
        return null;
    }

    @Override
    public boolean inactive(TriggerKey trigger) {
        return false;
    }

    @Override
    public boolean inactive(JobKey trigger) {
        return false;
    }

    @Override
    public boolean inactive(TriggerKey trigger, JobKey jobKey) {
        return false;
    }

    @Override
    public List<JobDetail> jobs() {
        return null;
    }

    @Override
    public List<JobDetail> jobsByTrigger(TriggerKey trigger) {
        return null;
    }

    @Override
    public List<Trigger> triggers() {
        return null;
    }

    @Override
    public List<JobDetail> getInactiveJobs() {
        return null;
    }

    @Override
    public List<Trigger> getInactiveTriggers() {
        return null;
    }

}

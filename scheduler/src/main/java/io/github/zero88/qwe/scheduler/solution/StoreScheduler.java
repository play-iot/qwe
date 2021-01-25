package io.github.zero88.qwe.scheduler.solution;

import java.util.List;

public interface StoreScheduler {

    JobKey add(Trigger trigger, JobDetail definition);

    TriggerKey remove(Trigger trigger);

    JobKey remove(JobDetail trigger);

    boolean inactive(TriggerKey trigger);

    boolean inactive(JobKey trigger);

    boolean inactive(TriggerKey trigger, JobKey jobKey);

    List<JobDetail> jobs();

    List<JobDetail> jobsByTrigger(TriggerKey trigger);

    List<Trigger> triggers();

    List<JobDetail> getInactiveJobs();

    List<Trigger> getInactiveTriggers();

}

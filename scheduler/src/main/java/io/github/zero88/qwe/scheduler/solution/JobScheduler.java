package io.github.zero88.qwe.scheduler.solution;

import io.vertx.core.Vertx;

public interface JobScheduler {

    static JobScheduler init(Vertx vertx) {
        return null;
    }

    StoreScheduler store();

    JobKey add(Trigger trigger, JobDetail definition);

    boolean cancel(JobKey jobKey);

    boolean cancel(TriggerKey triggerKey);

    boolean cancel(TriggerKey triggerKey, JobKey jobKey);

    boolean pause(JobKey jobKey);

    boolean pause(TriggerKey jobKey);

    boolean pause(TriggerKey triggerKey, JobKey jobKey);

    JobScheduler start();

}

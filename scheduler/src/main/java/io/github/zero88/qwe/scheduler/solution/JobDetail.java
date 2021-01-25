package io.github.zero88.qwe.scheduler.solution;

import io.github.zero88.qwe.scheduler.solution.Job.JobBuilder;

public interface JobDetail<J extends Job> {

    JobKey key();

    JobBuilder<J> builder();

    default int priority() {
        return 1000;
    }

}

package io.github.zero88.qwe.scheduler.solution;

public interface Job {

    void execute();

    interface JobBuilder<J extends Job> {

        J build();

    }

}

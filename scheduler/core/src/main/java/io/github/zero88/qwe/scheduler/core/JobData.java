package io.github.zero88.qwe.scheduler.core;

@FunctionalInterface
public interface JobData {

    <T> T get();

    JobData EMPTY = new JobData() {
        @Override
        public <T> T get() {
            return null;
        }
    };

}

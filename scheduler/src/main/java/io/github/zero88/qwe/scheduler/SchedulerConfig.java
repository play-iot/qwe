package io.github.zero88.qwe.scheduler;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.github.zero88.qwe.CarlConfig.AppConfig;
import io.github.zero88.qwe.IConfig;
import io.github.zero88.qwe.scheduler.service.SchedulerService;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchedulerConfig implements IConfig {

    @Default
    private final String schedulerName = "qwe";
    @Default
    private final String schedulerServiceClass = SchedulerService.class.getName();
    @Default
    private final String registerAddress = "qwe.scheduler.register";
    @Default
    private final String monitorAddress = "qwe.scheduler.monitor";
    @Default
    @JsonProperty(WorkerPoolConfig.NAME)
    private final WorkerPoolConfig workerConfig = WorkerPoolConfig.builder().build();

    @Override
    public String key() {
        return "__scheduler__";
    }

    @Override
    public Class<? extends IConfig> parent() {
        return AppConfig.class;
    }

    @NonNull
    public Class<? extends SchedulerService> schedulerServiceClass() {
        return Optional.ofNullable(ReflectionClass.<SchedulerService>findClass(schedulerServiceClass))
                       .orElse(SchedulerService.class);
    }

    public WorkerPoolConfig getWorkerConfig() {
        if (Strings.isBlank(workerConfig.getPoolName())) {
            workerConfig.createPoolName(schedulerName);
        }
        return workerConfig;
    }

    @Getter
    @Builder
    @Jacksonized
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class WorkerPoolConfig implements IConfig {

        public static final String NAME = "__schedule_worker__";
        @Default
        @Setter(value = AccessLevel.PACKAGE)
        private String poolName = "";
        @Default
        private final int poolSize = 5;
        @Default
        private final long maxExecuteTime = 60;
        @Default
        private final TimeUnit maxExecuteTimeUnit = TimeUnit.SECONDS;

        @Override
        public String key() {
            return NAME;
        }

        @Override
        public Class<? extends IConfig> parent() {
            return SchedulerConfig.class;
        }

        public void createPoolName(@NonNull String schedulerName) {
            this.poolName = "worker-pool-scheduler-" + schedulerName;
        }

    }

}

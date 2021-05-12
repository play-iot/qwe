package io.zero88.qwe.scheduler;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.zero88.qwe.QWEConfig.AppConfig;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.scheduler.service.SchedulerMonitorService;
import io.zero88.qwe.scheduler.service.SchedulerRegisterService;
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
    private final String registerAddress = "qwe.scheduler.register";
    @Default
    private final String registerServiceClass = SchedulerRegisterService.class.getName();
    @Default
    private final String monitorAddress = "qwe.scheduler.monitor";
    @Default
    private final String monitorServiceClass = SchedulerMonitorService.class.getName();
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
    public Class<? extends SchedulerRegisterService> registerServiceClass() {
        return Optional.ofNullable(ReflectionClass.<SchedulerRegisterService>findClass(registerServiceClass))
                       .orElse(SchedulerRegisterService.class);
    }

    @NonNull
    public Class<? extends SchedulerMonitorService> monitorServiceClass() {
        return Optional.ofNullable(ReflectionClass.<SchedulerMonitorService>findClass(monitorServiceClass))
                       .orElse(SchedulerMonitorService.class);
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

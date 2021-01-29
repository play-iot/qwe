package io.github.zero88.qwe.scheduler;

import java.nio.file.Path;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;

import io.github.zero88.qwe.component.Component;
import io.github.zero88.qwe.component.ComponentContext;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.exceptions.InitializerError;
import io.github.zero88.qwe.scheduler.job.QWEJobFactory;
import io.vertx.core.Vertx;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SchedulerContext extends ComponentContext {

    @Getter(value = AccessLevel.PACKAGE)
    private Scheduler scheduler;

    protected SchedulerContext(Class<? extends Component> componentClz, Path dataDir, String sharedKey,
                               String deployId) {
        super(componentClz, dataDir, sharedKey, deployId);
    }

    SchedulerContext init(SharedDataLocalProxy sharedData, SchedulerConfig config) {
        final Vertx vertx = sharedData.getVertx();
        try {
            final DirectSchedulerFactory factory = DirectSchedulerFactory.getInstance();
            factory.createScheduler(config.getSchedulerName(), config.getSchedulerName(),
                                    new QuartzVertxThreadPool(vertx, config.getWorkerConfig()), new RAMJobStore());
            scheduler = factory.getScheduler(config.getSchedulerName());
            scheduler.setJobFactory(new QWEJobFactory(sharedData, config));
            scheduler.start();
            return this;
        } catch (SchedulerException e) {
            throw new InitializerError("Cannot start QWE Scheduler", e);
        }
    }

    SchedulerContext shutdown() {
        try {
            scheduler.shutdown();
            scheduler = null;
        } catch (SchedulerException e) {
            log.warn("Cannot shutdown QWE Scheduler", e);
        }
        return this;
    }

}

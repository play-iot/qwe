package io.github.zero88.qwe.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;

import io.github.zero88.qwe.component.ComponentContext;
import io.github.zero88.qwe.component.ComponentContext.DefaultComponentContext;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.exceptions.InitializerError;
import io.github.zero88.qwe.scheduler.job.QWEJob;
import io.github.zero88.qwe.scheduler.quartz.QWEJobFactory;
import io.github.zero88.qwe.scheduler.quartz.QWEThreadPool;
import io.github.zero88.qwe.scheduler.service.SchedulerConverterHelper;
import io.github.zero88.qwe.scheduler.service.SchedulerMonitorService;
import io.github.zero88.qwe.scheduler.service.SchedulerRegisterService;
import io.github.zero88.qwe.utils.ExecutorHelpers;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SchedulerContext extends DefaultComponentContext {

    @Getter(value = AccessLevel.PACKAGE)
    private Scheduler scheduler;

    protected SchedulerContext(ComponentContext context) {
        super(context);
    }

    SchedulerContext init(SharedDataLocalProxy sharedData, SchedulerConfig config) {
        final Vertx vertx = sharedData.getVertx();
        ExecutorHelpers.blocking(vertx, () -> this.init(sharedData, config, vertx)).subscribe();
        return this;
    }

    private SchedulerContext init(SharedDataLocalProxy sharedData, SchedulerConfig config, Vertx vertx) {
        try {
            final DirectSchedulerFactory factory = DirectSchedulerFactory.getInstance();
            factory.createScheduler(config.getSchedulerName(), config.getSchedulerName(),
                                    new QWEThreadPool(vertx, config.getWorkerConfig()), new RAMJobStore());
            scheduler = factory.getScheduler(config.getSchedulerName());
            scheduler.setJobFactory(new QWEJobFactory(sharedData));
            scheduler.start();
            registerSchedulerEvent(sharedData, config);
            return this;
        } catch (SchedulerException e) {
            throw new InitializerError("Cannot start QWE Scheduler", e);
        }
    }

    private void registerSchedulerEvent(SharedDataLocalProxy sharedData, SchedulerConfig config) {
        final EventbusClient eventbus = EventbusClient.create(sharedData);
        sharedData.addData(QWEJob.MONITOR_ADDRESS_KEY, config.getMonitorAddress());
        eventbus.register(config.getRegisterAddress(),
                          SchedulerRegisterService.create(scheduler, sharedData, SchedulerConverterHelper.create(),
                                                          config.registerServiceClass()));
        eventbus.register(config.getMonitorAddress(), new SchedulerMonitorService(sharedData));
    }

    void shutdown(Vertx vertx, Promise<Void> future) {
        ExecutorHelpers.blocking(vertx, this::shutdown).subscribe(s -> future.complete(), future::fail);
    }

    private SchedulerContext shutdown() {
        try {
            scheduler.shutdown();
            scheduler = null;
        } catch (SchedulerException e) {
            log.warn("Cannot shutdown QWE Scheduler", e);
        }
        return this;
    }

}

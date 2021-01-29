package io.github.zero88.qwe.scheduler;

import io.github.zero88.qwe.component.ComponentContext;
import io.github.zero88.qwe.component.ComponentVerticle;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.scheduler.service.RegisterScheduleListener;
import io.github.zero88.qwe.utils.ExecutorHelpers;
import io.vertx.core.Promise;

import lombok.NonNull;

public final class SchedulerVerticle extends ComponentVerticle<SchedulerConfig, SchedulerContext> {

    SchedulerVerticle(SharedDataLocalProxy sharedData) {
        super(sharedData);
    }

    @Override
    public Class<SchedulerConfig> configClass() { return SchedulerConfig.class; }

    @Override
    public String configFile() { return "scheduler.json"; }

    @Override
    public void stop(Promise<Void> future) throws Exception {
        this.stop();
        ExecutorHelpers.blocking(vertx, () -> this.getContext().shutdown())
                       .subscribe(s -> future.complete(), future::fail);
    }

    @Override
    public SchedulerContext onSuccess(@NonNull ComponentContext context) {
        final SchedulerContext ctx = new SchedulerContext(context).init(sharedData(), config);
        EventbusClient.create(sharedData())
                      .register(config.getRegisterAddress(), new RegisterScheduleListener(ctx.getScheduler()));
        return ctx;
    }

}

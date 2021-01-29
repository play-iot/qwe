package io.github.zero88.qwe.scheduler;

import io.github.zero88.qwe.component.ComponentContext;
import io.github.zero88.qwe.component.ComponentVerticle;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
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
        this.getContext().shutdown(vertx, future);
    }

    @Override
    public SchedulerContext onSuccess(@NonNull ComponentContext context) {
        return new SchedulerContext(context).init(sharedData(), config);
    }

}

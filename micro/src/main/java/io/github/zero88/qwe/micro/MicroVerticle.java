package io.github.zero88.qwe.micro;

import io.github.zero88.qwe.component.ComponentContext;
import io.github.zero88.qwe.component.ComponentVerticle;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.vertx.core.Promise;

import lombok.NonNull;

public final class MicroVerticle extends ComponentVerticle<MicroConfig, MicroContext> {

    MicroVerticle(SharedDataLocalProxy sharedData) {
        super(sharedData);
    }

    @Override
    public void stop(Promise<Void> promise) { getContext().unregister(promise); }

    @Override
    public Class<MicroConfig> configClass() { return MicroConfig.class; }

    @Override
    public String configFile() { return "micro.json"; }

    @Override
    public MicroContext onSuccess(@NonNull ComponentContext context) {
        logger.info("Setup micro-service...");
        return new MicroContext(context).setup(vertx, config);
    }

}

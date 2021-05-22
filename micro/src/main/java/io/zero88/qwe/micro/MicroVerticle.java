package io.zero88.qwe.micro;

import io.vertx.core.Future;
import io.zero88.qwe.ComponentContext;
import io.zero88.qwe.ComponentVerticle;
import io.zero88.qwe.SharedDataLocalProxy;

import lombok.NonNull;

public final class MicroVerticle extends ComponentVerticle<MicroConfig, MicroContext> {

    MicroVerticle(SharedDataLocalProxy sharedData) {
        super(sharedData);
    }

    @Override
    public Future<Void> onAsyncStop() {
        return getContext().unregister();
    }

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

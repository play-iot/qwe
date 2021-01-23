package io.github.zero88.qwe.micro;

import java.nio.file.Path;

import io.github.zero88.qwe.IConfig;
import io.github.zero88.qwe.component.Component;
import io.github.zero88.qwe.component.ComponentVerticle;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.vertx.core.Promise;

import lombok.NonNull;

public final class Microservice extends ComponentVerticle<MicroConfig, MicroContext> {

    Microservice(SharedDataLocalProxy sharedData) {
        super(sharedData);
    }

    @Override
    public void stop(Promise<Void> promise) { getContext().unregister(promise); }

    @Override
    public Class<MicroConfig> configClass() { return MicroConfig.class; }

    @Override
    public String configFile() { return "micro.json"; }

    @Override
    public MicroContext onSuccess(@NonNull Class<Component<IConfig, MicroContext>> aClass, Path dataDir,
                                  String sharedKey, String deployId) {
        logger.info("Setup micro-service...");
        return new MicroContext(aClass, dataDir, sharedKey, deployId).setup(vertx, config);
    }

}

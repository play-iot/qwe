package io.github.zero88.storage.json;

import io.github.zero88.qwe.component.ComponentContext;
import io.github.zero88.qwe.component.ComponentVerticle;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.vertx.core.Promise;

import lombok.NonNull;

public class JsonStorageVerticle extends ComponentVerticle<StorageConfig, ComponentContext> {

    JsonStorageVerticle(@NonNull SharedDataLocalProxy sharedData) {
        super(sharedData);
    }

    @Override
    public @NonNull Class<StorageConfig> configClass() {
        return StorageConfig.class;
    }

    @Override
    public @NonNull String configFile() {
        return "storage.json";
    }

    @Override
    public void start() {
        super.start();
        this.config.makeFullPath((String) sharedData().getData(SharedDataLocalProxy.APP_DATADIR));
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);
    }

}

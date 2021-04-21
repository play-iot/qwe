package io.zero88.qwe.storage.json;

import io.zero88.qwe.component.ComponentContext;
import io.zero88.qwe.component.ComponentVerticle;
import io.zero88.qwe.component.SharedDataLocalProxy;
import io.zero88.qwe.event.EventbusClient;
import io.zero88.qwe.storage.json.service.JsonStorageService;

import lombok.NonNull;

public final class JsonStorageVerticle extends ComponentVerticle<StorageConfig, ComponentContext> {

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
        config.makeFullPath((String) sharedData().getData(SharedDataLocalProxy.APP_DATADIR));
        EventbusClient.create(sharedData())
                      .register(this.config.getServiceAddress(),
                                JsonStorageService.create(sharedData(), config, config.serviceHandlerClass()));
    }

}

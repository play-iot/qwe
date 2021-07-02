package io.zero88.qwe.storage.json;

import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginVerticle;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.storage.json.service.JsonStorageService;

import lombok.NonNull;

public final class JsonStoragePlugin extends PluginVerticle<StorageConfig, PluginContext> {

    JsonStoragePlugin(@NonNull SharedDataLocalProxy sharedData) {
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
    public void onStart() {
        pluginConfig.makeFullPath((String) sharedData().getData(SharedDataLocalProxy.APP_DATADIR_KEY));
        EventBusClient.create(sharedData())
                      .register(pluginConfig.getServiceAddress(),
                                JsonStorageService.create(pluginConfig, pluginConfig.serviceHandlerClass()));
    }

}

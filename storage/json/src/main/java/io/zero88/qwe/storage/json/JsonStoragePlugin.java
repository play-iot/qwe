package io.zero88.qwe.storage.json;

import java.util.Objects;

import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginVerticle;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.storage.json.service.JsonStorageService;

import lombok.NonNull;

public final class JsonStoragePlugin extends PluginVerticle<JsonStorageConfig, PluginContext> {

    JsonStoragePlugin(@NonNull SharedDataLocalProxy sharedData) {
        super(sharedData);
    }

    @Override
    public @NonNull Class<JsonStorageConfig> configClass() {
        return JsonStorageConfig.class;
    }

    @Override
    public @NonNull String configFile() {
        return "storage.json";
    }

    @Override
    public void onStart() {
        EventBusClient.create(sharedData())
                      .register(pluginConfig.getServiceAddress(),
                                JsonStorageService.create(Objects.requireNonNull(pluginContext().dataDir()),
                                                          pluginConfig, pluginConfig.serviceHandlerClass()));
    }

}

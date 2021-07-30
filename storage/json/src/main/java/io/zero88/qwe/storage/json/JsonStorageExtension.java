package io.zero88.qwe.storage.json;

import java.nio.file.Path;

import io.zero88.qwe.Extension;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class JsonStorageExtension implements Extension<JsonStorageConfig, JsonStorageService> {

    private JsonStorageConfig extConfig;
    private JsonStorageService entrypoint;

    @Override
    public @NonNull Class<JsonStorageConfig> configClass() {
        return JsonStorageConfig.class;
    }

    @Override
    public @NonNull String configFile() {
        return "storage.json";
    }

    @Override
    public String configKey() {
        return JsonStorageConfig.KEY;
    }

    @Override
    public JsonStorageExtension setup(SharedDataLocalProxy sharedData, String appName, Path appDir,
                                      JsonStorageConfig config) {
        extConfig = config == null ? JsonStorageConfig.create() : config;
        entrypoint = JsonStorageService.create(appDir, extConfig);
        EventBusClient.create(sharedData).register(extConfig.getServiceAddress(), entrypoint);
        return this;
    }

    @Override
    public void stop() {
    }

}

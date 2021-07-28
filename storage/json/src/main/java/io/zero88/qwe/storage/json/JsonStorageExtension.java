package io.zero88.qwe.storage.json;

import java.nio.file.Path;
import java.util.Objects;

import io.vertx.core.eventbus.impl.EventBusImpl;
import io.zero88.qwe.Extension;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class JsonStorageExtension implements Extension<JsonStorageConfig, JsonStorageService> {

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
    public JsonStorageExtension setup(JsonStorageConfig config, String appName, Path appDir,
                                      SharedDataLocalProxy sharedData) {
        entrypoint = JsonStorageService.create(Objects.requireNonNull(appDir), config);
        EventBusClient.create(sharedData).register(config.getServiceAddress(), entrypoint);
        return this;
    }

    @Override
    public void stop() {
    }

}

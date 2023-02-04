package cloud.playio.qwe.storage.json;

import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.Extension;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.crypto.CryptoContext;

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
    public JsonStorageExtension setup(SharedDataLocalProxy sharedData, String appName, Path appDir,
                                      @NotNull JsonObject config, @NotNull CryptoContext cryptoContext) {
        entrypoint = JsonStorageService.create(appDir, computeConfig(config));
        EventBusClient.create(sharedData).register(entrypoint.extConfig().getServiceAddress(), entrypoint);
        return this;
    }

    @Override
    public void stop() {
    }

}

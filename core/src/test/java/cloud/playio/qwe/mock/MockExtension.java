package cloud.playio.qwe.mock;

import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.Extension;
import cloud.playio.qwe.ExtensionEntrypoint;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.crypto.CryptoContext;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public class MockExtension
    implements Extension<MockExtensionConfig, MockExtension>, ExtensionEntrypoint<MockExtensionConfig> {

    @Getter
    @Accessors(fluent = true)
    private MockExtensionConfig extConfig;

    @Override
    public Extension<MockExtensionConfig, MockExtension> setup(SharedDataLocalProxy sharedData, String appName,
                                                               Path appDir, @NotNull JsonObject config,
                                                               @NotNull CryptoContext cryptoContext) {
        this.extConfig = computeConfig(config);
        return this;
    }

    @Override
    public void stop() {}

    @Override
    public MockExtension entrypoint() {
        return this;
    }

    @Override
    public @NonNull Class<MockExtensionConfig> configClass() {
        return MockExtensionConfig.class;
    }

    @Override
    public @NonNull String configFile() {
        return "mock-ext.json";
    }

    @Override
    public String configKey() {
        return MockExtensionConfig.KEY;
    }

    public static class MockErrorExtension extends MockExtension {

        @Override
        public Extension<MockExtensionConfig, MockExtension> setup(SharedDataLocalProxy sharedData, String appName,
                                                                   Path appDir, @NotNull JsonObject config,
                                                                   @NotNull CryptoContext cryptoContext) {
            throw new IllegalArgumentException("xxx");
        }

    }

}

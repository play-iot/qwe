package io.zero88.qwe.mock;

import java.nio.file.Path;

import io.zero88.qwe.Extension;
import io.zero88.qwe.ExtensionEntrypoint;
import io.zero88.qwe.SharedDataLocalProxy;

import lombok.NonNull;

public class MockExtension implements Extension<MockExtensionConfig, MockExtension>, ExtensionEntrypoint {

    private MockExtensionConfig extConfig;

    @Override
    public Extension<MockExtensionConfig, MockExtension> setup(SharedDataLocalProxy sharedData, String appName,
                                                               Path appDir, MockExtensionConfig config) {
        this.extConfig = config;
        return this;
    }

    @Override
    public void stop() {

    }

    @Override
    public MockExtensionConfig extConfig() {
        return extConfig;
    }

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
                                                                   Path appDir, MockExtensionConfig config) {
            throw new IllegalArgumentException("xxx");
        }

    }

}

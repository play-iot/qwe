package io.zero88.qwe.mock;

import java.nio.file.Path;

import io.zero88.qwe.Extension;
import io.zero88.qwe.ExtensionEntrypoint;
import io.zero88.qwe.SharedDataLocalProxy;

import lombok.NonNull;

public class MockExtension implements Extension<MockExtensionConfig, MockExtension>, ExtensionEntrypoint {

    @Override
    public Extension<MockExtensionConfig, MockExtension> setup(MockExtensionConfig config, String appName, Path appDir,
                                                               SharedDataLocalProxy sharedData) {
        return this;
    }

    @Override
    public void stop() {

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
        public Extension<MockExtensionConfig, MockExtension> setup(MockExtensionConfig config, String appName,
                                                                   Path appDir, SharedDataLocalProxy sharedData) {
            throw new IllegalArgumentException("xxx");
        }

    }

}

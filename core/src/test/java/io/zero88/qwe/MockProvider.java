package io.zero88.qwe;

import io.zero88.qwe.MockProvider.MockPluginVerticle;

import lombok.Getter;
import lombok.NonNull;

public class MockProvider implements PluginProvider<MockPluginVerticle> {

    private final boolean error;

    public MockProvider(boolean error) {this.error = error;}

    @Override
    public Class<MockPluginVerticle> pluginClass() { return MockPluginVerticle.class; }

    @Override
    public MockPluginVerticle provide(SharedDataLocalProxy proxy) {
        return new MockPluginVerticle(proxy, error);
    }

    static final class MockPluginVerticle extends PluginVerticle<MockConfig, PluginContext> {

        @Getter
        private final boolean error;

        public MockPluginVerticle(SharedDataLocalProxy sharedData) {
            this(sharedData, false);
        }

        @Override
        public String appName() {
            return "mock";
        }

        public MockPluginVerticle(SharedDataLocalProxy sharedData, boolean error) {
            super(sharedData);
            this.error = error;
        }

        @Override
        public @NonNull Class<MockConfig> configClass() {
            return MockConfig.class;
        }

        @Override
        public @NonNull String configFile() {
            return "config.json";
        }

        @Override
        public void onStart() {
            if (error) {
                throw new RuntimeException("Error when starting plugin Verticle");
            }
        }

    }

}

package io.zero88.qwe;

import io.zero88.qwe.MockProvider.MockPlugin;

import lombok.Getter;
import lombok.NonNull;

public class MockProvider implements PluginProvider<MockPlugin> {

    private final boolean error;

    public MockProvider(boolean error) {this.error = error;}

    @Override
    public Class<MockPlugin> pluginClass() { return MockPlugin.class; }

    @Override
    public MockPlugin provide(SharedDataLocalProxy proxy) {
        return new MockPlugin(proxy, error);
    }

    static final class MockPlugin extends PluginVerticle<MockConfig, PluginContext> {

        @Getter
        private final boolean error;

        public MockPlugin(SharedDataLocalProxy sharedData) {
            this(sharedData, false);
        }

        @Override
        public String pluginName() {
            return "mock";
        }

        public MockPlugin(SharedDataLocalProxy sharedData, boolean error) {
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
                throw new RuntimeException("Error when starting plugin[" + pluginName() + "]");
            }
        }

    }

}

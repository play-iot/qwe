package io.zero88.qwe.mock;

import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginProvider;
import io.zero88.qwe.PluginVerticle;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.mock.MockPluginProvider.MockPlugin;

import lombok.Getter;
import lombok.NonNull;

public class MockPluginProvider implements PluginProvider<MockPlugin> {

    private final boolean error;

    public MockPluginProvider()              {this(false);}

    public MockPluginProvider(boolean error) {this.error = error;}

    @Override
    public Class<MockPlugin> pluginClass() {return MockPlugin.class;}

    @Override
    public MockPlugin provide(SharedDataLocalProxy sharedData) {
        return new MockPlugin(sharedData, error);
    }

    static final class MockPlugin extends PluginVerticle<MockPluginConfig, PluginContext> {

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
        public @NonNull Class<MockPluginConfig> configClass() {
            return MockPluginConfig.class;
        }

        @Override
        public @NonNull String configFile() {
            return "config.json";
        }

        @Override
        public void onStart() {
            if (error) {
                throw new RuntimeException("Error when starting Plugin[" + pluginName() + "]");
            }
        }

        @Override
        public String configKey() {
            return "mock";
        }

    }

}

package io.zero88.qwe.mock;

import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginProvider;
import io.zero88.qwe.PluginVerticle;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.mock.DummyPluginProvider.DummyPlugin;

import lombok.NonNull;

public final class DummyPluginProvider implements PluginProvider<DummyPlugin> {

    @Override
    public Class<DummyPlugin> pluginClass() {return DummyPlugin.class;}

    @Override
    public DummyPlugin provide(SharedDataLocalProxy sharedData) {
        return new DummyPlugin(sharedData);
    }

    static final class DummyPlugin extends PluginVerticle<MockPluginConfig, PluginContext> {

        DummyPlugin(SharedDataLocalProxy sharedData) {
            super(sharedData);
        }

        @Override
        public String pluginName() {
            return "dummy";
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
        public String configKey() {
            return "mock";
        }

    }

}

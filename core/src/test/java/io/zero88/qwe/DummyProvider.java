package io.zero88.qwe;

import io.zero88.qwe.DummyProvider.DummyPlugin;

import lombok.NonNull;

final class DummyProvider implements PluginProvider<DummyPlugin> {

    @Override
    public Class<DummyPlugin> pluginClass() { return DummyPlugin.class; }

    @Override
    public DummyPlugin provide(SharedDataLocalProxy proxy) {
        return new DummyPlugin(proxy);
    }

    static final class DummyPlugin extends PluginVerticle<MockConfig, PluginContext> {

        DummyPlugin(SharedDataLocalProxy sharedData) {
            super(sharedData);
        }

        @Override
        public String pluginName() {
            return "dummy";
        }

        @Override
        public @NonNull Class<MockConfig> configClass() {
            return MockConfig.class;
        }

        @Override
        public @NonNull String configFile() {
            return "config.json";
        }

    }

}

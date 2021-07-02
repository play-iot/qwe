package io.zero88.qwe;

import io.zero88.qwe.DummyProvider.DummyPluginVerticle;

import lombok.NonNull;

final class DummyProvider implements PluginProvider<DummyPluginVerticle> {

    @Override
    public Class<DummyPluginVerticle> pluginClass() { return DummyPluginVerticle.class; }

    @Override
    public DummyPluginVerticle provide(SharedDataLocalProxy proxy) {
        return new DummyPluginVerticle(proxy);
    }

    static final class DummyPluginVerticle extends PluginVerticle<MockConfig, PluginContext> {

        DummyPluginVerticle(SharedDataLocalProxy sharedData) {
            super(sharedData);
        }

        @Override
        public String appName() {
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

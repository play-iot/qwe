package cloud.playio.qwe.mock;

import cloud.playio.qwe.PluginContext;
import cloud.playio.qwe.PluginProvider;
import cloud.playio.qwe.PluginVerticle;
import cloud.playio.qwe.mock.DummyPluginProvider.DummyPlugin;

import lombok.NonNull;

public final class DummyPluginProvider implements PluginProvider<DummyPlugin> {

    @Override
    public Class<DummyPlugin> pluginClass() {return DummyPlugin.class;}

    @Override
    public DummyPlugin get() {
        return new DummyPlugin();
    }

    static final class DummyPlugin extends PluginVerticle<MockPluginConfig, PluginContext> {

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

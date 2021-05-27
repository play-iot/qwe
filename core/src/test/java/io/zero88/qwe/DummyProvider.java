package io.zero88.qwe;

import io.zero88.qwe.DummyProvider.DummyComponentVerticle;

import lombok.NonNull;

final class DummyProvider implements ComponentProvider<DummyComponentVerticle> {

    @Override
    public Class<DummyComponentVerticle> componentClass() { return DummyComponentVerticle.class; }

    @Override
    public DummyComponentVerticle provide(SharedDataLocalProxy proxy) {
        return new DummyComponentVerticle(proxy);
    }

    static final class DummyComponentVerticle extends ComponentVerticle<MockConfig, ComponentContext> {

        DummyComponentVerticle(SharedDataLocalProxy sharedData) {
            super(sharedData);
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

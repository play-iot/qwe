package io.github.zero88.qwe.component;

import io.github.zero88.qwe.component.DummyProvider.DummyComponentVerticle;

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

        @Override
        public void start() {
            logger.info("Starting Fake Unit Verticle...");
            super.start();
        }

    }

}
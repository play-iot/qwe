package io.zero88.qwe;

import io.zero88.qwe.MockProvider.MockComponent;

import lombok.Getter;
import lombok.NonNull;

public class MockProvider implements ComponentProvider<MockComponent> {

    private final boolean error;

    public MockProvider(boolean error) {this.error = error;}

    @Override
    public Class<MockComponent> componentClass() { return MockComponent.class; }

    @Override
    public MockComponent provide(SharedDataLocalProxy proxy) {
        return new MockComponent(proxy, error);
    }

    static final class MockComponent extends ComponentVerticle<MockConfig, ComponentContext> {

        @Getter
        private final boolean error;

        public MockComponent(SharedDataLocalProxy sharedData) {
            this(sharedData, false);
        }

        public MockComponent(SharedDataLocalProxy sharedData, boolean error) {
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
        public void start() {
            logger.info("Starting Mock Unit Verticle...");
            super.start();
            if (error) {
                throw new RuntimeException("Error when starting Component Verticle");
            }
        }

    }

}

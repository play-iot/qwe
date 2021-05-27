package io.zero88.qwe;

import io.zero88.qwe.MockProvider.MockComponentVerticle;

import lombok.Getter;
import lombok.NonNull;

public class MockProvider implements ComponentProvider<MockComponentVerticle> {

    private final boolean error;

    public MockProvider(boolean error) {this.error = error;}

    @Override
    public Class<MockComponentVerticle> componentClass() { return MockComponentVerticle.class; }

    @Override
    public MockComponentVerticle provide(SharedDataLocalProxy proxy) {
        return new MockComponentVerticle(proxy, error);
    }

    static final class MockComponentVerticle extends ComponentVerticle<MockConfig, ComponentContext> {

        @Getter
        private final boolean error;

        public MockComponentVerticle(SharedDataLocalProxy sharedData) {
            this(sharedData, false);
        }

        public MockComponentVerticle(SharedDataLocalProxy sharedData, boolean error) {
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
                throw new RuntimeException("Error when starting Component Verticle");
            }
        }

    }

}

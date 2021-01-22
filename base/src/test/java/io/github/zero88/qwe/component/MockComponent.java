package io.github.zero88.qwe.component;

import io.github.zero88.qwe.utils.mock.MockConfig;

import lombok.Getter;
import lombok.NonNull;

public final class MockComponent extends ComponentVerticle<MockConfig, ComponentContext> {

    @Getter
    private final boolean error;

    public MockComponent() {
        this(false);
    }

    public MockComponent(boolean error) {
        super(ComponentContext.VOID);
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
            throw new RuntimeException("Error when starting Unit Verticle");
        }
    }

}

package io.github.zero88.msa.blueprint.component;

import io.github.zero88.msa.blueprint.utils.mock.MockConfig;

import lombok.Getter;
import lombok.NonNull;

public final class MockUnitVerticle extends UnitVerticle<MockConfig, UnitContext> {

    @Getter
    private final boolean error;

    public MockUnitVerticle() {
        this(false);
    }

    public MockUnitVerticle(boolean error) {
        super(UnitContext.VOID);
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

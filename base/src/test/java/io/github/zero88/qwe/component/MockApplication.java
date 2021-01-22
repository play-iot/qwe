package io.github.zero88.qwe.component;

import lombok.Setter;

public final class MockApplication extends ApplicationVerticle {

    @Setter
    private boolean error;
    @Setter
    private boolean errorInHandler;

    @Override
    public void start() {
        logger.info("Starting Mock Container Verticle...");
        super.start();
        if (error) {
            throw new RuntimeException("Error when starting");
        }
        registerSuccessHandler(errorInHandler ? v -> {
            throw new IllegalArgumentException("Error in success handler");
        } : v -> logger.info("No error"));
    }

    @Override
    public String configFile() {
        return "mock-container.json";
    }

}

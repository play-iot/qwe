package io.zero88.qwe;

import lombok.Setter;

@Setter
public final class MockApplication extends ApplicationVerticle {

    private boolean error;
    private boolean errorInHandler;

    @Override
    public void onStart() {
        logger.info("Starting Mock Container Verticle...");
        if (error) {
            throw new RuntimeException("Error when starting");
        }
    }

    @Override
    public void onInstallCompleted(ContextLookup lookup) {
        if (errorInHandler) {
            throw new IllegalArgumentException("Error in success handler");
        }
    }

    @Override
    public String configFile() {
        return "mock-container.json";
    }

}

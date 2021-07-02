package io.zero88.qwe;

import io.vertx.core.Handler;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true)
public final class MockApplication extends ApplicationVerticle {

    private boolean errorOnStart;
    private boolean errorOnCompleted;
    private Handler<PluginContextLookup> onCompleted;

    @Override
    public void onStart() {
        if (errorOnStart) {
            throw new RuntimeException("Error when starting");
        }
    }

    @Override
    public void onInstallCompleted(PluginContextLookup lookup) {
        if (errorOnCompleted) {
            throw new IllegalArgumentException("Error onInstallCompleted");
        }
        if (onCompleted != null) {
            onCompleted.handle(lookup);
        }
    }

    @Override
    public String configFile() {
        return "mock-container.json";
    }

}

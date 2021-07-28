package io.zero88.qwe.micro;

import java.nio.file.Path;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.zero88.qwe.Extension;
import io.zero88.qwe.SharedDataLocalProxy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CircuitBreakerExtension
    implements CircuitBreakerProvider, Extension<CircuitBreakerConfig, CircuitBreakerProvider> {

    private CircuitBreaker circuitBreaker;

    @Override
    public String configKey() {
        return CircuitBreakerConfig.KEY;
    }

    @Override
    public @NonNull Class<CircuitBreakerConfig> configClass() {
        return CircuitBreakerConfig.class;
    }

    @Override
    public @NonNull String configFile() {
        return "circuitBreaker.json";
    }

    @Override
    public CircuitBreakerExtension entrypoint() {
        return this;
    }

    @Override
    public CircuitBreakerExtension setup(CircuitBreakerConfig config, String appName, Path appDir,
                                         SharedDataLocalProxy sharedData) {
        circuitBreaker = CircuitBreaker.create(appName + "-" + config.getExtName(), sharedData.getVertx(),
                                               config.getOptions());
        return this;
    }

    @Override
    public void stop() {
        circuitBreaker.close();
    }

    @Override
    public CircuitBreaker get() {
        return this.circuitBreaker;
    }

}

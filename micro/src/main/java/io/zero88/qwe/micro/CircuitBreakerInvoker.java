package io.zero88.qwe.micro;

import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zero88.qwe.micro.MicroConfig.CircuitBreakerConfig;
import io.reactivex.Single;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.Vertx;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CircuitBreakerInvoker implements Supplier<CircuitBreaker> {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerInvoker.class);

    private final CircuitBreaker circuitBreaker;

    static CircuitBreakerInvoker create(Vertx vertx, CircuitBreakerConfig config) {
        if (config.isEnabled()) {
            logger.info("Circuit Breaker Config : {}", config.toJson().encode());
            return new CircuitBreakerInvoker(
                CircuitBreaker.create(config.getCircuitName(), vertx, config.getOptions()));
        }
        logger.info("Skip setup circuit breaker");
        return new CircuitBreakerInvoker(null);
    }

    @Override
    public CircuitBreaker get() {
        return Objects.requireNonNull(this.circuitBreaker, "Circuit breaker is not enabled");
    }

    public <T> Single<T> wrap(Single<T> command) {
        if (Objects.isNull(circuitBreaker)) {
            return command;
        }
        return getRx().rxExecute(event -> command.subscribe(event::complete, event::fail));
    }

    private io.vertx.reactivex.circuitbreaker.CircuitBreaker getRx() {
        return io.vertx.reactivex.circuitbreaker.CircuitBreaker.newInstance(get());
    }

}

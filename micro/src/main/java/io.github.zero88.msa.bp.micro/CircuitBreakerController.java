package io.github.zero88.msa.bp.micro;

import java.util.Objects;
import java.util.function.Supplier;

import io.github.zero88.msa.bp.micro.MicroConfig.CircuitBreakerConfig;
import io.reactivex.Single;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CircuitBreakerController implements Supplier<CircuitBreaker> {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    private final CircuitBreaker circuitBreaker;

    static CircuitBreakerController create(Vertx vertx, CircuitBreakerConfig config) {
        if (config.isEnabled()) {
            logger.info("Circuit Breaker Config : {}", config.toJson().encode());
            return new CircuitBreakerController(
                CircuitBreaker.create(config.getCircuitName(), vertx, config.getOptions()));
        }
        logger.info("Skip setup circuit breaker");
        return new CircuitBreakerController(null);
    }

    @Override
    public CircuitBreaker get() {
        return Objects.requireNonNull(this.circuitBreaker, "Circuit breaker is not enabled");
    }

    public <T> Single<T> wrap(Single<T> command) {
        if (Objects.isNull(circuitBreaker)) {
            return command;
        }
        return getRx().rxExecuteCommand(event -> command.subscribe(event::complete, event::fail));
    }

    private io.vertx.reactivex.circuitbreaker.CircuitBreaker getRx() {
        return io.vertx.reactivex.circuitbreaker.CircuitBreaker.newInstance(get());
    }

}

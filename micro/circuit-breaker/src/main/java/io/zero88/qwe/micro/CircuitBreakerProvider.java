package io.zero88.qwe.micro;

import java.util.Objects;
import java.util.function.Supplier;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.Future;
import io.zero88.qwe.ExtensionEntrypoint;

public interface CircuitBreakerProvider extends Supplier<CircuitBreaker>, ExtensionEntrypoint {

    default <T> Future<T> wrap(Future<T> command) {
        if (Objects.isNull(get())) {
            return command;
        }
        return get().execute(command::onComplete);
    }

}

package io.zero88.qwe.micro;

import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.zero88.qwe.IConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public final class CircuitBreakerConfig implements IConfig {

    public static final String NAME = "__circuitBreaker__";
    public static final String DEFAULT_NOTIFICATION_ADDRESS = "qwe.circuit.breaker";

    @JsonProperty(value = "name")
    private String circuitName = "qwe-circuit-breaker";
    private boolean enabled = false;
    private CircuitBreakerOptions options = new CircuitBreakerOptions().setNotificationAddress(
        DEFAULT_NOTIFICATION_ADDRESS);

    @Override
    public String configKey() {return NAME;}

}

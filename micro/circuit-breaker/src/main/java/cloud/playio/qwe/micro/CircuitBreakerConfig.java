package cloud.playio.qwe.micro;

import java.util.Optional;

import io.github.zero88.utils.Strings;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import cloud.playio.qwe.ExtensionConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public final class CircuitBreakerConfig implements ExtensionConfig {

    public static final String KEY = "__circuitBreaker__";

    private String extName = "cb";
    private CircuitBreakerOptions options = new CircuitBreakerOptions().setNotificationAddress("qwe.circuit-breaker");

    @Override
    public String configKey() {return KEY;}

    public CircuitBreakerConfig setOptions(CircuitBreakerOptions options) {
        CircuitBreakerOptions o = Optional.ofNullable(options).orElseGet(CircuitBreakerOptions::new);
        if (CircuitBreakerOptions.DEFAULT_NOTIFICATION_ADDRESS.equals(o.getNotificationAddress()) ||
            Strings.isBlank(o.getNotificationAddress())) {
            o.setNotificationAddress("qwe.circuit-breaker");
        }
        this.options = o;
        return this;
    }

}

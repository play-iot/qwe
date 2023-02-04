package cloud.playio.qwe.micro;

import org.junit.jupiter.api.Test;

import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.IConfig;
import cloud.playio.qwe.JsonHelper;

class CircuitBreakerConfigTest {

    @Test
    void test_default() {
        CircuitBreakerConfig def = new CircuitBreakerConfig();
        CircuitBreakerConfig cp = IConfig.fromClasspath("circuitBreaker.json", CircuitBreakerConfig.class);
        JsonHelper.assertJson(cp.toJson(), def.toJson());
    }

    @Test
    void test_serialize_deserialize() {
        CircuitBreakerConfig config = new CircuitBreakerConfig().setExtName("xy")
                                                                .setOptions(new CircuitBreakerOptions().setMaxRetries(3));
        JsonObject json = new JsonObject("{\"extName\":\"xy\",\"options\":{\"timeout\":10000,\"maxFailures\":5," +
                                         "\"fallbackOnFailure\":false,\"resetTimeout\":30000," +
                                         "\"notificationAddress\":\"qwe.circuit-breaker\"," +
                                         "\"notificationPeriod\":2000,\"maxRetries\":3," +
                                         "\"metricsRollingWindow\":10000,\"metricsRollingBuckets\":10," +
                                         "\"failuresRollingWindow\":10000}}");
        CircuitBreakerConfig parse = IConfig.from(json, CircuitBreakerConfig.class);
        JsonHelper.assertJson(parse.toJson(), config.toJson());
    }

}

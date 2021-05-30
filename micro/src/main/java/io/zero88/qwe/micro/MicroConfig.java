package io.zero88.qwe.micro;

import io.github.zero88.utils.Strings;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.impl.DefaultServiceDiscoveryBackend;
import io.zero88.qwe.ComponentConfig;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.zero88.qwe.micro.monitor.ServiceGatewayUsageMonitor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class MicroConfig implements ComponentConfig {

    public static final String KEY = "__micro__";

    @Default
    @JsonProperty(value = GatewayConfig.NAME)
    private final GatewayConfig gatewayConfig = new GatewayConfig();
    @Default
    @JsonProperty(value = ServiceDiscoveryConfig.NAME)
    private final ServiceDiscoveryConfig discoveryConfig = new ServiceDiscoveryConfig();
    @Default
    @JsonProperty(value = CircuitBreakerConfig.NAME)
    private final CircuitBreakerConfig circuitConfig = new CircuitBreakerConfig();

    @Override
    public String key() { return KEY; }

    @Getter
    public static class ServiceDiscoveryConfig extends ServiceDiscoveryOptions implements IConfig {

        public static final String NAME = "__serviceDiscovery__";

        public static final String SERVICE_DISCOVERY_ANNOUNCE_ADDRESS = "qwe.service.discovery.announce";
        public static final String SERVICE_DISCOVERY_USAGE_ADDRESS = "qwe.service.discovery.usage";
        static final String BACKEND_NAME = "backend-name";

        private final String backendClass;

        ServiceDiscoveryConfig() { this(null); }

        ServiceDiscoveryConfig(String backendClass) {
            this.backendClass = Strings.fallback(backendClass, DefaultServiceDiscoveryBackend.class.getName());
            this.reload();
        }

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return MicroConfig.class; }

        private void reload() {
            String announceAddress = this.getAnnounceAddress();
            this.setAnnounceAddress(Strings.isBlank(announceAddress) || DEFAULT_ANNOUNCE_ADDRESS.equals(announceAddress)
                                    ? SERVICE_DISCOVERY_ANNOUNCE_ADDRESS
                                    : announceAddress);
            if (DEFAULT_USAGE_ADDRESS.equals(getUsageAddress())) {
                setUsageAddress(SERVICE_DISCOVERY_USAGE_ADDRESS);
            }
            if (Strings.isNotBlank(backendClass) &&
                Strings.isBlank(this.getBackendConfiguration().getString(BACKEND_NAME))) {
                this.getBackendConfiguration().put(BACKEND_NAME, backendClass);
            }
        }

        @Override
        public JsonObject toJson(ObjectMapper mapper) {
            return super.toJson();
        }

    }


    @Getter
    public static class CircuitBreakerConfig implements IConfig {

        public static final String NAME = "__circuitBreaker__";
        public static final String DEFAULT_NOTIFICATION_ADDRESS = "qwe.circuit.breaker";

        @JsonProperty(value = "name")
        private String circuitName = "qwe-circuit-breaker";
        private boolean enabled = false;
        private CircuitBreakerOptions options = new CircuitBreakerOptions().setNotificationAddress(
            DEFAULT_NOTIFICATION_ADDRESS);

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return MicroConfig.class; }

    }


    @Getter
    @Setter
    public static class GatewayConfig implements IConfig {

        static final String NAME = "__gateway__";

        private boolean enabled = false;
        private String indexAddress = "qwe.service.gateway.index";
        private String announceMonitorClass = ServiceGatewayAnnounceMonitor.class.getName();
        private String usageMonitorClass = ServiceGatewayUsageMonitor.class.getName();

        @Override
        public String key() {
            return NAME;
        }

        @Override
        public Class<? extends IConfig> parent() {
            return MicroConfig.class;
        }

    }

}

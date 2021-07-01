package io.zero88.qwe.micro;

import io.github.zero88.utils.Strings;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.impl.DefaultServiceDiscoveryBackend;
import io.zero88.qwe.IConfig;

import lombok.Getter;

@Getter
public final class ServiceDiscoveryConfig extends ServiceDiscoveryOptions implements IConfig {

    public static final String NAME = "__serviceDiscovery__";

    public static final String SERVICE_DISCOVERY_ANNOUNCE_ADDRESS = "qwe.service.discovery.announce";
    public static final String SERVICE_DISCOVERY_USAGE_ADDRESS = "qwe.service.discovery.usage";
    static final String BACKEND_NAME = "backend-name";

    private final String backendClass;
    private final boolean cleanupBeforeStop;

    /**
     * For json creator
     */
    ServiceDiscoveryConfig() { this(null); }

    ServiceDiscoveryConfig(String backendClass) {
        this.setUsageAddress(null);
        this.backendClass = Strings.fallback(backendClass, DefaultServiceDiscoveryBackend.class.getName());
        this.cleanupBeforeStop = false;
        this.reload();
    }

    @Override
    public String key() { return NAME; }

    @Override
    public Class<? extends IConfig> parent() { return MicroConfig.class; }

    private void reload() {
        String announceAddress = this.getAnnounceAddress();
        this.setAnnounceAddress(
            Strings.isBlank(announceAddress) || ServiceDiscoveryOptions.DEFAULT_ANNOUNCE_ADDRESS.equals(announceAddress)
            ? SERVICE_DISCOVERY_ANNOUNCE_ADDRESS
            : announceAddress);
        if (ServiceDiscoveryOptions.DEFAULT_USAGE_ADDRESS.equals(getUsageAddress())) {
            setUsageAddress(SERVICE_DISCOVERY_USAGE_ADDRESS);
        }
        if (Strings.isNotBlank(backendClass) &&
            Strings.isBlank(this.getBackendConfiguration().getString(BACKEND_NAME))) {
            this.getBackendConfiguration().put(BACKEND_NAME, backendClass);
        }
    }

}

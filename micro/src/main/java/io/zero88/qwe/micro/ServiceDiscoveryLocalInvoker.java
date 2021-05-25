package io.zero88.qwe.micro;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.micro.MicroConfig.LocalServiceDiscoveryConfig;
import io.zero88.qwe.micro.type.ServiceKind;
import io.zero88.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.zero88.qwe.micro.monitor.ServiceGatewayUsageMonitor;
import io.vertx.core.eventbus.EventBus;

import lombok.NonNull;

final class ServiceDiscoveryLocalInvoker extends ServiceDiscoveryInvoker {

    ServiceDiscoveryLocalInvoker(SharedDataLocalProxy proxy, LocalServiceDiscoveryConfig config,
                                 CircuitBreakerInvoker circuitController) {
        super(proxy, config, createServiceDiscovery(proxy.getVertx(), config, ServiceKind.LOCAL, v -> true),
              circuitController);
    }

    @Override
    public <T extends ServiceGatewayAnnounceMonitor> void subscribe(EventBus eventBus, T announceMonitor) {
        eventBus.localConsumer(config.getAnnounceAddress(), announceMonitor);
    }

    @Override
    public <T extends ServiceGatewayUsageMonitor> void subscribe(EventBus eventBus, @NonNull T usageMonitor) {
        eventBus.localConsumer(config.getUsageAddress(), usageMonitor);
    }

    @Override
    ServiceKind kind() {
        return ServiceKind.LOCAL;
    }

    @Override
    String computeINet(String host) {
        return host;
    }

}

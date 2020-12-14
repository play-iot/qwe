package io.github.zero88.msa.bp.micro;

import io.github.zero88.msa.bp.micro.MicroConfig.LocalServiceDiscoveryConfig;
import io.github.zero88.msa.bp.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.github.zero88.msa.bp.micro.monitor.ServiceGatewayUsageMonitor;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import lombok.NonNull;

final class LocalSDController extends ServiceDiscoveryController {

    LocalSDController(Vertx vertx, LocalServiceDiscoveryConfig config, String sharedKey,
                      CircuitBreakerController circuitController) {
        super(config, sharedKey, createServiceDiscovery(vertx, config, ServiceKind.LOCAL, v -> true),
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

package io.github.zero88.msa.bp.micro;

import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import io.github.zero88.msa.bp.micro.MicroConfig.ServiceDiscoveryConfig;
import io.github.zero88.msa.bp.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.github.zero88.msa.bp.micro.monitor.ServiceGatewayUsageMonitor;
import io.github.zero88.msa.bp.utils.Networks;

import lombok.NonNull;

final class ClusterSDController extends ServiceDiscoveryController {

    ClusterSDController(Vertx vertx, ServiceDiscoveryConfig config, String sharedKey,
                        CircuitBreakerController circuitController) {
        super(config, sharedKey, createServiceDiscovery(vertx, config, ServiceKind.CLUSTER, Vertx::isClustered),
              circuitController);
    }

    @Override
    <T extends ServiceGatewayAnnounceMonitor> void subscribe(EventBus eventBus, @NonNull T announceMonitor) {
        eventBus.consumer(config.getAnnounceAddress(), announceMonitor);
    }

    @Override
    <T extends ServiceGatewayUsageMonitor> void subscribe(EventBus eventBus, @NonNull T usageMonitor) {
        if (Strings.isNotBlank(config.getUsageAddress())) {
            eventBus.consumer(config.getUsageAddress(), usageMonitor);
        }
    }

    @Override
    ServiceKind kind() {
        return ServiceKind.CLUSTER;
    }

    @Override
    String computeINet(String host) {
        return Networks.computeNATAddress(host);
    }

}

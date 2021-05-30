package io.zero88.qwe.micro;

import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.zero88.qwe.ComponentContext;
import io.zero88.qwe.ComponentVerticle;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.micro.MicroConfig.GatewayConfig;
import io.zero88.qwe.micro.MicroConfig.ServiceDiscoveryConfig;
import io.zero88.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.zero88.qwe.micro.monitor.ServiceGatewayUsageMonitor;

import lombok.NonNull;

public final class MicroVerticle extends ComponentVerticle<MicroConfig, MicroContext> {

    MicroVerticle(SharedDataLocalProxy sharedData) {
        super(sharedData);
    }

    @Override
    public Future<Void> onAsyncStop() {
        return componentContext().getDiscovery().unregister();
    }

    @Override
    public Class<MicroConfig> configClass() { return MicroConfig.class; }

    @Override
    public String configFile() { return "micro.json"; }

    @Override
    public MicroContext onSuccess(@NonNull ComponentContext context) {
        logger().info("Setup service discovery...");
        CircuitBreakerWrapper breaker = CircuitBreakerWrapper.create(vertx, componentConfig.getCircuitConfig());
        ServiceDiscoveryConfig discoveryConfig = componentConfig.getDiscoveryConfig();
        return new MicroContext(context).setup(setupGateway(new ServiceDiscoveryWrapper(sharedData(), discoveryConfig, breaker),
                         discoveryConfig.getAnnounceAddress(), discoveryConfig.getUsageAddress()));
    }

    private ServiceDiscoveryWrapper setupGateway(ServiceDiscoveryWrapper serviceDiscovery, String announceAddress,
                                                 String usageAddress) {
        final GatewayConfig gwCfg = componentConfig.getGatewayConfig();
        if (!gwCfg.isEnabled()) {
            logger().info("Skip setup service discovery gateway");
            return serviceDiscovery;
        }
        vertx.eventBus()
             .consumer(announceAddress, ServiceGatewayAnnounceMonitor.create(sharedData(), serviceDiscovery,
                                                                             gwCfg.getAnnounceMonitorClass()));
        if (Strings.isNotBlank(componentConfig.getDiscoveryConfig().getUsageAddress())) {
            vertx.eventBus()
                 .consumer(usageAddress, ServiceGatewayUsageMonitor.create(sharedData(), serviceDiscovery,
                                                                           gwCfg.getUsageMonitorClass()));
        }
        EventBusClient.create(sharedData()).register(gwCfg.getIndexAddress(), new ServiceLocator(serviceDiscovery));
        return serviceDiscovery;
    }

}

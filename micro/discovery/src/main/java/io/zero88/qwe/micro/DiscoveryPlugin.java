package io.zero88.qwe.micro;

import java.util.Objects;

import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginVerticle;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.zero88.qwe.micro.monitor.ServiceGatewayUsageMonitor;

import lombok.NonNull;

public final class DiscoveryPlugin extends PluginVerticle<MicroConfig, DiscoveryContext> {

    DiscoveryPlugin(SharedDataLocalProxy sharedData) {
        super(sharedData);
    }

    @Override
    public String pluginName() {
        return "micro-discovery";
    }

    @Override
    public Class<MicroConfig> configClass() { return MicroConfig.class; }

    @Override
    public String configFile() { return "micro.json"; }

    @Override
    public Future<Void> onAsyncStop() {
        ServiceDiscoveryConfig discoveryConfig = pluginConfig.lookup(ServiceDiscoveryConfig.NAME,
                                                                     ServiceDiscoveryConfig.class);
        if (discoveryConfig != null && discoveryConfig.isCleanupBeforeStop()) {
            return pluginContext().getDiscovery().unregister(new RequestFilter()).mapEmpty();
        }
        return Future.succeededFuture();
    }

    @Override
    public DiscoveryContext enrichContext(@NonNull PluginContext pluginContext, boolean isPostStep) {
        final DiscoveryContext discoveryContext = new DiscoveryContext(pluginContext);
        if (!isPostStep) {
            return discoveryContext;
        }
        logger().info("Setup service discovery...");
        CircuitBreakerWrapper breaker = CircuitBreakerWrapper.create(vertx,
                                                                     pluginConfig.lookup(CircuitBreakerConfig.NAME,
                                                                                         CircuitBreakerConfig.class));
        ServiceDiscoveryConfig discoveryConfig = pluginConfig.lookup(ServiceDiscoveryConfig.NAME,
                                                                     ServiceDiscoveryConfig.class);
        ServiceDiscoveryApi discoveryApi = new ServiceDiscoveryApiImpl(sharedData(), discoveryConfig, breaker);
        return discoveryContext.setup(setupGateway(discoveryApi, discoveryConfig));
    }

    private ServiceDiscoveryApi setupGateway(ServiceDiscoveryApi serviceDiscovery,
                                             ServiceDiscoveryConfig discoveryConfig) {
        ServiceGatewayConfig gwCfg = pluginConfig.lookup(ServiceGatewayConfig.NAME, ServiceGatewayConfig.class);
        if (Objects.isNull(gwCfg) || !gwCfg.isEnabled()) {
            logger().info("Skip setup service discovery gateway");
            return serviceDiscovery;
        }
        vertx.eventBus()
             .consumer(discoveryConfig.getAnnounceAddress(),
                       ServiceGatewayAnnounceMonitor.create(sharedData(), serviceDiscovery,
                                                            gwCfg.getAnnounceMonitorClass()));
        if (Strings.isNotBlank(discoveryConfig.getUsageAddress())) {
            vertx.eventBus()
                 .consumer(discoveryConfig.getUsageAddress(),
                           ServiceGatewayUsageMonitor.create(sharedData(), serviceDiscovery,
                                                             gwCfg.getUsageMonitorClass()));
        }
        EventBusClient.create(sharedData()).register(gwCfg.getIndexAddress(), new ServiceLocator(serviceDiscovery));
        return serviceDiscovery;
    }

}

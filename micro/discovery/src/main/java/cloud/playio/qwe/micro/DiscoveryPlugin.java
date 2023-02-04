package cloud.playio.qwe.micro;

import java.util.Objects;

import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import cloud.playio.qwe.PluginContext;
import cloud.playio.qwe.PluginVerticle;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import cloud.playio.qwe.micro.monitor.ServiceGatewayUsageMonitor;

import lombok.NonNull;

public final class DiscoveryPlugin extends PluginVerticle<MicroConfig, DiscoveryContext> {

    @Override
    public String pluginName() {
        return "service-discovery";
    }

    @Override
    public Class<MicroConfig> configClass() {return MicroConfig.class;}

    @Override
    public String configKey() {
        return MicroConfig.KEY;
    }

    @Override
    public String configFile() {return "discovery.json";}

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
        ServiceDiscoveryConfig discoveryConfig = pluginConfig.lookup(ServiceDiscoveryConfig.NAME,
                                                                     ServiceDiscoveryConfig.class);
        ServiceDiscoveryApi discoveryApi = new ServiceDiscoveryApiImpl(sharedData(), discoveryConfig);
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

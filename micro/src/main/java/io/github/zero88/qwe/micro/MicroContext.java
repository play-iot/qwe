package io.github.zero88.qwe.micro;

import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

import io.github.zero88.qwe.component.Component;
import io.github.zero88.qwe.component.ComponentContext;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.micro.MicroConfig.GatewayConfig;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MicroContext extends ComponentContext {

    @Getter
    private CircuitBreakerController breakerController;
    @Getter
    private ServiceDiscoveryController clusterController;
    @Getter
    private ServiceDiscoveryController localController;

    MicroContext() {
        //TODO
        this(Microservice.class, null, MicroContext.class.getName(), UUID.randomUUID().toString());
    }

    protected MicroContext(Class<? extends Component> clazz, Path dataDir, String sharedKey, String deployId) {
        super(clazz, dataDir, sharedKey, deployId);
    }

    MicroContext setup(Vertx vertx, MicroConfig config) {
        final SharedDataLocalProxy proxy = SharedDataLocalProxy.create(vertx, sharedKey());
        this.breakerController = CircuitBreakerController.create(vertx, config.getCircuitConfig());
        this.clusterController = new ClusterSDController(proxy, config.getDiscoveryConfig(), this.breakerController);
        this.localController = new LocalSDController(proxy, config.getLocalDiscoveryConfig(), this.breakerController);
        setupGateway(proxy, config.getGatewayConfig(), clusterController, localController);
        return this;
    }

    private void setupGateway(SharedDataLocalProxy proxy, GatewayConfig config,
                              ServiceDiscoveryController clusterController,
                              ServiceDiscoveryController localController) {
        if (!config.isEnabled()) {
            log.info("Skip setup service discovery gateway");
            return;
        }
        log.info("Service Discovery Gateway Config : {}", config.toJson().encode());
        if (proxy.getVertx().isClustered()) {
            clusterController.subscribe(proxy.getVertx(), config.getClusterAnnounceMonitorClass(),
                                        config.getClusterUsageMonitorClass());
        }
        localController.subscribe(proxy.getVertx(), config.getLocalAnnounceMonitorClass(),
                                  config.getLocalUsageMonitorClass());
        EventbusClient.create(proxy).register(config.getIndexAddress(), new ServiceGatewayIndex(this));
    }

    void unregister(Promise<Void> promise) {
        this.clusterController.unregister(promise);
        this.localController.unregister(promise);
    }

    public void rescanService(EventBus eventBus) {
        if (Objects.nonNull(clusterController)) {
            clusterController.rescanService(eventBus);
        }
        if (Objects.nonNull(localController)) {
            localController.rescanService(eventBus);
        }
    }

}

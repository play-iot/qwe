package io.github.zero88.qwe.micro;

import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

import io.github.zero88.qwe.component.Component;
import io.github.zero88.qwe.component.ComponentContext;
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
        this.breakerController = CircuitBreakerController.create(vertx, config.getCircuitConfig());
        this.clusterController = new ClusterSDController(vertx, config.getDiscoveryConfig(), sharedKey(),
                                                         this.breakerController);
        this.localController = new LocalSDController(vertx, config.getLocalDiscoveryConfig(), sharedKey(),
                                                     this.breakerController);
        setupGateway(vertx, config.getGatewayConfig(), clusterController, localController, sharedKey());
        return this;
    }

    private void setupGateway(Vertx vertx, GatewayConfig config, ServiceDiscoveryController clusterController,
                              ServiceDiscoveryController localController, String sharedKey) {
        if (!config.isEnabled()) {
            log.info("Skip setup service discovery gateway");
            return;
        }
        log.info("Service Discovery Gateway Config : {}", config.toJson().encode());
        if (vertx.isClustered()) {
            clusterController.subscribe(vertx, config.getClusterAnnounceMonitorClass(),
                                        config.getClusterUsageMonitorClass());
        }
        localController.subscribe(vertx, config.getLocalAnnounceMonitorClass(), config.getLocalUsageMonitorClass());
        EventbusClient.create(vertx, sharedKey).register(config.getIndexAddress(), new ServiceGatewayIndex(this));
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

package io.github.zero88.msa.bp.micro;

import java.util.Objects;

import io.github.zero88.msa.bp.component.SharedDataDelegate;
import io.github.zero88.msa.bp.component.UnitContext;
import io.github.zero88.msa.bp.micro.MicroConfig.GatewayConfig;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class MicroContext extends UnitContext {

    @Getter
    private CircuitBreakerController breakerController;
    @Getter
    private ServiceDiscoveryController clusterController;
    @Getter
    private ServiceDiscoveryController localController;

    /**
     * For test only
     */
    MicroContext setup(Vertx vertx, MicroConfig config) {
        return setup(vertx, config, MicroContext.class.getName());
    }

    MicroContext setup(Vertx vertx, MicroConfig config, String sharedKey) {
        this.breakerController = CircuitBreakerController.create(vertx, config.getCircuitConfig());
        this.clusterController = new ClusterSDController(vertx, config.getDiscoveryConfig(), sharedKey,
                                                         this.breakerController);
        this.localController = new LocalSDController(vertx, config.getLocalDiscoveryConfig(), sharedKey,
                                                     this.breakerController);
        setupGateway(vertx, config.getGatewayConfig(), clusterController, localController, sharedKey);
        return this;
    }

    private void setupGateway(Vertx vertx, GatewayConfig config, ServiceDiscoveryController clusterController,
                              ServiceDiscoveryController localController, String sharedKey) {
        if (!config.isEnabled()) {
            logger.info("Skip setup service discovery gateway");
            return;
        }
        logger.info("Service Discovery Gateway Config : {}", config.toJson().encode());
        if (vertx.isClustered()) {
            clusterController.subscribe(vertx, config.getClusterAnnounceMonitorClass(),
                                        config.getClusterUsageMonitorClass());
        }
        localController.subscribe(vertx, config.getLocalAnnounceMonitorClass(), config.getLocalUsageMonitorClass());
        SharedDataDelegate.getEventController(vertx, sharedKey)
                          .register(config.getIndexAddress(), new ServiceGatewayIndex(this));
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

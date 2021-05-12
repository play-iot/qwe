package io.zero88.qwe.micro;

import java.util.Objects;
import java.util.UUID;

import io.zero88.qwe.ComponentContext;
import io.zero88.qwe.ComponentContext.DefaultComponentContext;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.micro.MicroConfig.GatewayConfig;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MicroContext extends DefaultComponentContext {

    @Getter
    private CircuitBreakerInvoker breakerInvoker;
    @Getter
    private ServiceDiscoveryInvoker clusterInvoker;
    @Getter
    private ServiceDiscoveryInvoker localInvoker;

    MicroContext() {
        //FIXME data dir for test
        super(MicroVerticle.class, null, MicroContext.class.getName(), UUID.randomUUID().toString());
    }

    protected MicroContext(@NonNull ComponentContext context) {
        super(context);
    }

    MicroContext setup(Vertx vertx, MicroConfig config) {
        final SharedDataLocalProxy proxy = SharedDataLocalProxy.create(vertx, sharedKey());
        this.breakerInvoker = CircuitBreakerInvoker.create(vertx, config.getCircuitConfig());
        this.clusterInvoker = new ServiceDiscoveryClusterInvoker(proxy, config.getDiscoveryConfig(),
                                                                 this.breakerInvoker);
        this.localInvoker = new ServiceDiscoveryLocalInvoker(proxy, config.getLocalDiscoveryConfig(),
                                                             this.breakerInvoker);
        setupGateway(proxy, config.getGatewayConfig(), clusterInvoker, localInvoker);
        return this;
    }

    private void setupGateway(SharedDataLocalProxy proxy, GatewayConfig config,
                              ServiceDiscoveryInvoker clusterDiscovery, ServiceDiscoveryInvoker localDiscovery) {
        if (!config.isEnabled()) {
            log.info("Skip setup service discovery gateway");
            return;
        }
        log.info("Service Discovery Gateway Config : {}", config.toJson().encode());
        if (proxy.getVertx().isClustered()) {
            clusterDiscovery.subscribe(proxy.getVertx(), config.getClusterAnnounceMonitorClass(),
                                       config.getClusterUsageMonitorClass());
        }
        localDiscovery.subscribe(proxy.getVertx(), config.getLocalAnnounceMonitorClass(),
                                 config.getLocalUsageMonitorClass());
        EventBusClient.create(proxy).register(config.getIndexAddress(), new ServiceLocator(this));
    }

    void unregister(Promise<Void> promise) {
        this.clusterInvoker.unregister(promise);
        this.localInvoker.unregister(promise);
    }

    public void rescanService(EventBus eventBus) {
        if (Objects.nonNull(clusterInvoker)) {
            clusterInvoker.rescanService(eventBus);
        }
        if (Objects.nonNull(localInvoker)) {
            localInvoker.rescanService(eventBus);
        }
    }

}

package io.github.zero88.msa.bp.micro.monitor;

import io.github.zero88.msa.bp.micro.ServiceDiscoveryController;
import io.github.zero88.msa.bp.micro.monitor.ServiceGatewayMonitor.AbstractServiceGatewayMonitor;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

import lombok.Getter;

@Getter
public class ServiceGatewayUsageMonitor extends AbstractServiceGatewayMonitor {

    protected ServiceGatewayUsageMonitor(Vertx vertx, ServiceDiscoveryController controller, String sharedKey) {
        super(vertx, controller, sharedKey);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServiceGatewayUsageMonitor> T create(Vertx vertx, ServiceDiscoveryController controller,
                                                                  String sharedKey, String className) {
        return (T) ServiceGatewayMonitor.create(vertx, controller, sharedKey, className,
                                                ServiceGatewayUsageMonitor.class);
    }

    @Override
    public void handle(Message<Object> message) {
        if (logger.isTraceEnabled()) {
            logger.trace("SERVICE USAGE GATEWAY::Receive message from: '{}' - Headers: '{}' - Body: '{}'",
                         message.address(), message.headers(), message.body());
        }
    }

}

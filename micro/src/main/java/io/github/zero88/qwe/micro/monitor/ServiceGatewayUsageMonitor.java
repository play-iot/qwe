package io.github.zero88.qwe.micro.monitor;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.micro.ServiceDiscoveryInvoker;
import io.github.zero88.qwe.micro.monitor.ServiceGatewayMonitor.AbstractServiceGatewayMonitor;
import io.vertx.core.eventbus.Message;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ServiceGatewayUsageMonitor extends AbstractServiceGatewayMonitor {

    protected ServiceGatewayUsageMonitor(@NonNull SharedDataLocalProxy sharedData,
                                         @NonNull ServiceDiscoveryInvoker controller) {
        super(sharedData, controller);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServiceGatewayUsageMonitor> T create(SharedDataLocalProxy sharedData,
                                                                  ServiceDiscoveryInvoker controller,
                                                                  String className) {
        return (T) ServiceGatewayMonitor.create(sharedData, controller, className, ServiceGatewayUsageMonitor.class);
    }

    @Override
    public void handle(Message<Object> message) {
        if (logger.isTraceEnabled()) {
            logger.trace("SERVICE USAGE GATEWAY::Receive message from: '{}' - Headers: '{}' - Body: '{}'",
                         message.address(), message.headers(), message.body());
        }
    }

}

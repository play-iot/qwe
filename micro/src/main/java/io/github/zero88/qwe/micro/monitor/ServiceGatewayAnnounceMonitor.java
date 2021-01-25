package io.github.zero88.qwe.micro.monitor;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.micro.ServiceDiscoveryInvoker;
import io.github.zero88.qwe.micro.monitor.ServiceGatewayMonitor.AbstractServiceGatewayMonitor;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ServiceGatewayAnnounceMonitor extends AbstractServiceGatewayMonitor {

    protected ServiceGatewayAnnounceMonitor(@NonNull SharedDataLocalProxy proxy,
                                            @NonNull ServiceDiscoveryInvoker controller) {
        super(proxy, controller);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServiceGatewayAnnounceMonitor> T create(SharedDataLocalProxy proxy,
                                                                     ServiceDiscoveryInvoker controller,
                                                                     String className) {
        return (T) ServiceGatewayMonitor.create(proxy, controller, className, ServiceGatewayAnnounceMonitor.class);
    }

    protected void handle(Record record) { }

    @Override
    public final void handle(Message<Object> message) {
        String msg = "SERVICE ANNOUNCEMENT GATEWAY::Receive message from:";
        logger.info("{} '{}'", msg, message.address());
        if (logger.isTraceEnabled()) {
            logger.trace("{} '{}' - Headers: '{}' - Body: '{}'", msg, message.address(), message.headers(),
                         message.body());
        }
        handle(new Record((JsonObject) message.body()));
    }

}

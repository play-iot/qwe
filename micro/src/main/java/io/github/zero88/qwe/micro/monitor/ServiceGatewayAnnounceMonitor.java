package io.github.zero88.qwe.micro.monitor;

import io.github.zero88.qwe.micro.ServiceDiscoveryController;
import io.github.zero88.qwe.micro.monitor.ServiceGatewayMonitor.AbstractServiceGatewayMonitor;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import lombok.Getter;

@Getter
public class ServiceGatewayAnnounceMonitor extends AbstractServiceGatewayMonitor {

    protected ServiceGatewayAnnounceMonitor(Vertx vertx, ServiceDiscoveryController controller, String sharedKey) {
        super(vertx, controller, sharedKey);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServiceGatewayAnnounceMonitor> T create(Vertx vertx, ServiceDiscoveryController controller,
                                                                     String sharedKey, String className) {
        return (T) ServiceGatewayMonitor.create(vertx, controller, sharedKey, className,
                                                ServiceGatewayAnnounceMonitor.class);
    }

    protected void handle(Record record) { }

    @Override
    public final void handle(Message<Object> message) {
        String msg = "SERVICE ANNOUNCEMENT GATEWAY::Receive message from:";
        logger.info("{} '{}'", msg, message.address());
        if (logger.isTraceEnabled()) {
            logger.trace("{} '{}' - Headers: '{}' - Body: '{}'", message.address(), message.headers(), message.body());
        }
        handle(new Record((JsonObject) message.body()));
    }

}

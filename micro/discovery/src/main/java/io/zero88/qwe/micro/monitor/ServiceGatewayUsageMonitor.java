package io.zero88.qwe.micro.monitor;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.micro.ServiceDiscoveryApi;
import io.zero88.qwe.micro.monitor.ServiceGatewayMonitor.AbstractServiceGatewayMonitor;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ServiceGatewayUsageMonitor extends AbstractServiceGatewayMonitor<UsageInfo> {

    protected ServiceGatewayUsageMonitor(@NonNull SharedDataLocalProxy sharedData,
                                         @NonNull ServiceDiscoveryApi controller) {
        super(sharedData, controller);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServiceGatewayUsageMonitor> T create(SharedDataLocalProxy sharedData,
                                                                  ServiceDiscoveryApi controller, String className) {
        return (T) ServiceGatewayMonitor.create(sharedData, controller, className, ServiceGatewayUsageMonitor.class);
    }

    @Override
    String function() {
        return "SERVICE USAGE GATEWAY";
    }

    @Override
    protected UsageInfo parse(Message<Object> message) {
        return UsageInfo.parse((JsonObject) trace(message).body());
    }

    @Override
    protected void process(UsageInfo record) {

    }

}

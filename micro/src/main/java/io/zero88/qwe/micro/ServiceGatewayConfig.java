package io.zero88.qwe.micro;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.zero88.qwe.micro.monitor.ServiceGatewayUsageMonitor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceGatewayConfig implements IConfig {

    static final String NAME = "__gateway__";

    private boolean enabled = false;
    private String indexAddress = "qwe.service.gateway.index";
    private String announceMonitorClass = ServiceGatewayAnnounceMonitor.class.getName();
    private String usageMonitorClass = ServiceGatewayUsageMonitor.class.getName();

    @Override
    public String key() {
        return NAME;
    }

    @Override
    public Class<? extends IConfig> parent() {
        return MicroConfig.class;
    }

}

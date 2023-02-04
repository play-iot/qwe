package cloud.playio.qwe.micro;

import cloud.playio.qwe.IConfig;
import cloud.playio.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import cloud.playio.qwe.micro.monitor.ServiceGatewayUsageMonitor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ServiceGatewayConfig implements IConfig {

    static final String NAME = "__gateway__";

    private boolean enabled = false;
    private String indexAddress = "qwe.service.gateway.index";
    private String announceMonitorClass = ServiceGatewayAnnounceMonitor.class.getName();
    private String usageMonitorClass = ServiceGatewayUsageMonitor.class.getName();

    @Override
    public String configKey() {
        return NAME;
    }

    @Override
    public Class<? extends IConfig> parent() {
        return MicroConfig.class;
    }

}

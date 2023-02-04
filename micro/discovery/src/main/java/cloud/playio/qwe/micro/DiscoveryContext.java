package cloud.playio.qwe.micro;

import cloud.playio.qwe.PluginContext;
import cloud.playio.qwe.PluginContext.DefaultPluginContext;
import lombok.Getter;
import lombok.NonNull;

public final class DiscoveryContext extends DefaultPluginContext {

    @Getter
    private ServiceDiscoveryApi discovery;

    DiscoveryContext(@NonNull PluginContext context) {
        super(context);
    }

    DiscoveryContext setup(ServiceDiscoveryApi discovery) {
        this.discovery = discovery;
        return this;
    }

}

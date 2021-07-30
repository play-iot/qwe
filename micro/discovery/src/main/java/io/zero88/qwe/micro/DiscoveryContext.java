package io.zero88.qwe.micro;

import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginContext.DefaultPluginContext;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

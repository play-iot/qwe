package io.zero88.qwe.micro;

import io.zero88.qwe.PluginProvider;
import io.zero88.qwe.SharedDataLocalProxy;

public final class DiscoveryPluginProvider implements PluginProvider<DiscoveryPlugin> {

    @Override
    public Class<DiscoveryPlugin> pluginClass() { return DiscoveryPlugin.class; }

    @Override
    public DiscoveryPlugin provide(SharedDataLocalProxy proxy) {
        return new DiscoveryPlugin(proxy);
    }

}

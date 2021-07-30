package io.zero88.qwe.micro;

import java.util.Collection;
import java.util.Collections;

import io.zero88.qwe.Extension;
import io.zero88.qwe.PluginProvider;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.client.HttpClientExtension;

public final class DiscoveryPluginProvider implements PluginProvider<DiscoveryPlugin> {

    @Override
    public Class<DiscoveryPlugin> pluginClass() {return DiscoveryPlugin.class;}

    @Override
    public DiscoveryPlugin provide(SharedDataLocalProxy sharedData) {
        return new DiscoveryPlugin(sharedData);
    }

    @Override
    public Collection<Class<? extends Extension>> extensions() {
        return Collections.singleton(HttpClientExtension.class);
    }

}

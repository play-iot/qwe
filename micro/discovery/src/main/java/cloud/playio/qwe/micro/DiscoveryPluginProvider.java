package cloud.playio.qwe.micro;

import java.util.Collection;
import java.util.Collections;

import cloud.playio.qwe.Extension;
import cloud.playio.qwe.PluginProvider;
import cloud.playio.qwe.http.client.HttpClientExtension;

public final class DiscoveryPluginProvider implements PluginProvider<DiscoveryPlugin> {

    @Override
    public Class<DiscoveryPlugin> pluginClass() {return DiscoveryPlugin.class;}

    @Override
    public DiscoveryPlugin get() {
        return new DiscoveryPlugin();
    }

    @Override
    public Collection<Class<? extends Extension>> extensions() {
        return Collections.singleton(HttpClientExtension.class);
    }

}

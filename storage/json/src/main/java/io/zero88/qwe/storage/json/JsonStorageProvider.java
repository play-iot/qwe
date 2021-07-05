package io.zero88.qwe.storage.json;

import io.zero88.qwe.PluginProvider;
import io.zero88.qwe.SharedDataLocalProxy;

public final class JsonStorageProvider implements PluginProvider<JsonStoragePlugin> {

    @Override
    public Class<JsonStoragePlugin> pluginClass() {
        return JsonStoragePlugin.class;
    }

    @Override
    public JsonStoragePlugin provide(SharedDataLocalProxy sharedData) {
        return new JsonStoragePlugin(sharedData);
    }

}

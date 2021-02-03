package io.github.zero88.qwe.storage.json;

import io.github.zero88.qwe.component.ComponentProvider;
import io.github.zero88.qwe.component.SharedDataLocalProxy;

public final class JsonStorageProvider implements ComponentProvider<JsonStorageVerticle> {

    @Override
    public Class<JsonStorageVerticle> componentClass() {
        return JsonStorageVerticle.class;
    }

    @Override
    public JsonStorageVerticle provide(SharedDataLocalProxy proxy) {
        return new JsonStorageVerticle(proxy);
    }

}

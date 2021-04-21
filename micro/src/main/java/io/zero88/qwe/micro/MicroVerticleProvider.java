package io.zero88.qwe.micro;

import io.zero88.qwe.component.ComponentProvider;
import io.zero88.qwe.component.SharedDataLocalProxy;

public final class MicroVerticleProvider implements ComponentProvider<MicroVerticle> {

    @Override
    public Class<MicroVerticle> componentClass() { return MicroVerticle.class; }

    @Override
    public MicroVerticle provide(SharedDataLocalProxy proxy) {
        return new MicroVerticle(proxy);
    }

}

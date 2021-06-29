package io.zero88.qwe.micro;

import io.zero88.qwe.ComponentContext;
import io.zero88.qwe.ComponentContext.DefaultComponentContext;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MicroContext extends DefaultComponentContext {

    @Getter
    private ServiceDiscoveryApi discovery;

    protected MicroContext(@NonNull ComponentContext context) {
        super(context);
    }

    MicroContext setup(ServiceDiscoveryApi discovery) {
        this.discovery = discovery;
        return this;
    }

}

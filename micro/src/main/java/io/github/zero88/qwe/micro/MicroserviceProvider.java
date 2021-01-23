package io.github.zero88.qwe.micro;

import io.github.zero88.qwe.component.ComponentProvider;
import io.github.zero88.qwe.component.SharedDataLocalProxy;

public final class MicroserviceProvider implements ComponentProvider<Microservice> {

    @Override
    public Class<Microservice> componentClass() { return Microservice.class; }

    @Override
    public Microservice provide(SharedDataLocalProxy proxy) {
        return new Microservice(proxy);
    }

}

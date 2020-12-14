package io.github.zero88.msa.bp.micro;

import io.github.zero88.msa.bp.component.UnitProvider;

public final class MicroserviceProvider implements UnitProvider<Microservice> {

    @Override
    public Microservice get() { return new Microservice(); }

    @Override
    public Class<Microservice> unitClass() { return Microservice.class; }

}

package io.github.zero88.msa.blueprint.utils.mock;

import io.github.zero88.msa.blueprint.component.MockUnitVerticle;
import io.github.zero88.msa.blueprint.component.UnitProvider;

import lombok.Getter;
import lombok.Setter;

public class MockProvider implements UnitProvider<MockUnitVerticle> {

    @Getter
    @Setter
    private MockUnitVerticle unitVerticle;

    @Override
    public Class<MockUnitVerticle> unitClass() { return MockUnitVerticle.class; }

    @Override
    public MockUnitVerticle get() { return unitVerticle; }

}

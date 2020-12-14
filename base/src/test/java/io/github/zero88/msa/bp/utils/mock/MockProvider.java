package io.github.zero88.msa.bp.utils.mock;

import io.github.zero88.msa.bp.component.MockUnitVerticle;
import io.github.zero88.msa.bp.component.UnitProvider;

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

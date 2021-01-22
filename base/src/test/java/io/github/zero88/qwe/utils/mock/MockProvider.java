package io.github.zero88.qwe.utils.mock;

import io.github.zero88.qwe.component.MockComponent;
import io.github.zero88.qwe.component.ComponentProvider;

import lombok.Getter;
import lombok.Setter;

public class MockProvider implements ComponentProvider<MockComponent> {

    @Getter
    @Setter
    private MockComponent unitVerticle;

    @Override
    public Class<MockComponent> unitClass() { return MockComponent.class; }

    @Override
    public MockComponent get() { return unitVerticle; }

}

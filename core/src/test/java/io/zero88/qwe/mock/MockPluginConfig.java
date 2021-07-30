package io.zero88.qwe.mock;

import io.zero88.qwe.PluginConfig;

import lombok.Getter;
import lombok.Setter;

public class MockPluginConfig implements PluginConfig {

    @Setter
    @Getter
    private String name;

    @Override
    public String configKey() {
        return "mock";
    }

}

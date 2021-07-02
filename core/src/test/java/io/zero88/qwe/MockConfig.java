package io.zero88.qwe;

import lombok.Getter;
import lombok.Setter;

public class MockConfig implements PluginConfig {

    @Setter
    @Getter
    private String name;

    @Override
    public String key() {
        return "mock";
    }

}

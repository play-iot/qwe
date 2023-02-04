package cloud.playio.qwe.mock;

import cloud.playio.qwe.PluginConfig;

import lombok.Getter;
import lombok.Setter;

public class MockPluginConfig implements PluginConfig {

    public static final String MOCK_CFG_KEY = "mock";
    @Setter
    @Getter
    private String name;

    @Override
    public String configKey() {
        return MOCK_CFG_KEY;
    }

}

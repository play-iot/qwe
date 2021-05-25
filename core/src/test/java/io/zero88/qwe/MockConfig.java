package io.zero88.qwe;

import lombok.Getter;
import lombok.Setter;

public class MockConfig implements IConfig {

    @Setter
    @Getter
    private String name;

    @Override
    public String key() {
        return "mock";
    }

    @Override
    public Class<? extends IConfig> parent() {
        return QWEAppConfig.class;
    }

}

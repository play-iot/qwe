package io.zero88.qwe.mock;

import io.zero88.qwe.ExtensionConfig;

public class MockExtensionConfig implements ExtensionConfig {

    public static final String KEY = "__mock_ext__";

    @Override
    public String configKey() {
        return KEY;
    }

}

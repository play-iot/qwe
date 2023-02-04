package cloud.playio.qwe.mock;

import cloud.playio.qwe.ExtensionConfig;

public class MockExtensionConfig implements ExtensionConfig {

    public static final String KEY = "__mock_ext__";

    @Override
    public String configKey() {
        return KEY;
    }

}

package io.zero88.qwe;

import io.zero88.qwe.crypto.CryptoContext;

public interface BasePluginTest extends BaseAppContextTest {

    @Override
    default String appName() {
        return "PluginTest";
    }

    default CryptoContext pluginCryptoContext() {
        return CryptoContext.empty();
    }

    @SuppressWarnings("rawtypes")
    interface PluginDeployTest<T extends Plugin> extends BasePluginTest {

        PluginConfig initConfig();

        PluginProvider<T> initProvider();

    }

}

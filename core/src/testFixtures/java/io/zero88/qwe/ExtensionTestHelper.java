package io.zero88.qwe;

import java.nio.file.Path;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.Vertx;
import io.zero88.qwe.security.CryptoContext;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface ExtensionTestHelper extends AppContextTest {

    @Override
    default String appName() {
        return "ExtensionTest";
    }

    default <C extends ExtensionConfig, EE extends ExtensionEntrypoint<C>, E extends Extension<C, EE>> E initExtension(
        Vertx vertx, Class<E> cls, C extConfig) {
        return (E) createExt(createSharedData(vertx), appName(), testDir(), cls, extConfig);
    }

    static Extension createExt(SharedDataLocalProxy sharedData, String appName, Path appDir,
                               Class<? extends Extension> cls, ExtensionConfig extConfig) {
        return ReflectionClass.createObject(cls)
                              .setup(sharedData, appName, appDir, extConfig.toJson(), CryptoContext.empty());
    }

}

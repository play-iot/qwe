package io.zero88.qwe;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.Vertx;

public interface ExtensionTestHelper extends AppContextTest {

    @Override
    default String appName() {
        return "ExtensionTest";
    }

    @SuppressWarnings("unchecked")
    default <C extends ExtensionConfig, EE extends ExtensionEntrypoint, E extends Extension<C, EE>> E initExtension(
        Vertx vertx, Class<E> cls, C extensionConfig) {
        return (E) ReflectionClass.createObject(cls)
                                  .setup(extensionConfig, appName(), testDir(), createSharedData(vertx));
    }

}

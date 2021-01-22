package io.github.zero88.qwe.component;

import java.nio.file.Path;

import io.github.zero88.utils.Strings;

public interface ComponentVerticleTestHelper {

    static void injectTest(ComponentVerticle verticle, String sharedKey, Path path) {
        verticle.injectTest(Strings.isBlank(verticle.getSharedKey()) ? sharedKey : verticle.getSharedKey(), path);
    }

}

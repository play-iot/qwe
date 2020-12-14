package io.github.zero88.msa.blueprint.component;

import java.nio.file.Path;

import io.github.zero88.utils.Strings;

public interface UnitVerticleTestHelper {

    static void injectTest(UnitVerticle verticle, String sharedKey, Path path) {
        verticle.injectTest(Strings.isBlank(verticle.getSharedKey()) ? sharedKey : verticle.getSharedKey(), path);
    }

}

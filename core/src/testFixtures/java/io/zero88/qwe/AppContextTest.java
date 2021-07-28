package io.zero88.qwe;

import java.nio.file.Path;

import io.github.zero88.utils.UUID64;
import io.vertx.core.Vertx;

public interface AppContextTest {

    Path testDir();

    default String sharedKey() {
        return getClass().getName() + "--" + UUID64.random();
    }

    default SharedDataLocalProxy createSharedData(Vertx vertx) {
        return SharedDataLocalProxy.create(vertx, sharedKey());
    }

}

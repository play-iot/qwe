package io.zero88.qwe;

import io.vertx.core.Future;

public interface VerticleLifecycleHooks {

    default void onStart() {}

    default Future<Void> onAsyncStart() {
        return Future.succeededFuture();
    }

    default void onStop() {}

    default Future<Void> onAsyncStop() {
        return Future.succeededFuture();
    }

}

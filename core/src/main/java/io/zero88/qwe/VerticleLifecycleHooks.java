package io.zero88.qwe;

import io.vertx.core.Future;

/**
 * Represents for verticle lifecycle hook that inject any action {@code onStart} and {@code onStop}
 */
public interface VerticleLifecycleHooks {

    /**
     * You can do any non-block action when starting {@code Vertx verticle} in here
     */
    default void onStart() {}

    /**
     * You can do async action when starting {@code Vertx verticle} in here
     */
    default Future<Void> onAsyncStart() {
        return Future.succeededFuture();
    }

    /**
     * You can do any non-block action when before stopped {@code Vertx verticle} in here
     */
    default void onStop() {}

    /**
     * You can do async action when before stopped {@code Vertx verticle} in here
     */
    default Future<Void> onAsyncStop() {
        return Future.succeededFuture();
    }

}

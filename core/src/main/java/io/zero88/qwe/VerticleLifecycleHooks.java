package io.zero88.qwe;

import java.util.concurrent.CompletableFuture;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public interface VerticleLifecycleHooks {

    default void onStart() {}

    default Future<Void> onAsyncStart() {
        return Future.succeededFuture();
    }

    default void onStop() {}

    default Future<Void> onAsyncStop() {
        return Future.succeededFuture();
    }

    static Future<Void> run(Vertx vertx, Runnable runnable) {
        return Future.fromCompletionStage(CompletableFuture.runAsync(runnable), vertx.getOrCreateContext());
    }

}

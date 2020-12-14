package io.github.zero88.msa.blueprint.utils;

import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.reactivex.RxHelper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExecutorHelpers {

    public static <T> Single<T> blocking(@NonNull Vertx vertx, @NonNull Callable<T> callable) {
        return Single.fromCallable(callable).subscribeOn(RxHelper.blockingScheduler(vertx));
    }

    public static <T> Maybe<T> blocking(@NonNull Vertx vertx, @NonNull Maybe<T> stream) {
        return stream.subscribeOn(RxHelper.blockingScheduler(vertx));
    }

    public static <T> Single<T> blocking(@NonNull Vertx vertx, @NonNull Single<T> stream) {
        return stream.subscribeOn(RxHelper.blockingScheduler(vertx));
    }

    public static <T> Observable<T> blocking(@NonNull Vertx vertx, @NonNull Observable<T> stream) {
        return stream.subscribeOn(RxHelper.blockingScheduler(vertx));
    }

    public static <T> Flowable<T> blocking(@NonNull Vertx vertx, @NonNull Flowable<T> stream) {
        return stream.subscribeOn(RxHelper.blockingScheduler(vertx));
    }

}

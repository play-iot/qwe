package io.zero88.qwe.utils;

import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FutureHelpers {

    public static <T> Future<List<T>> flatten(@NonNull CompositeFuture compositeFuture, @NonNull Class<T> cls) {
        return compositeFuture.flatMap(
            s -> Future.succeededFuture(s.list().stream().map(cls::cast).collect(Collectors.toList())));
    }

}

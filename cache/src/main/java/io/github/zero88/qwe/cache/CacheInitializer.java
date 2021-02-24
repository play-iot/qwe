package io.github.zero88.qwe.cache;

import java.util.function.Supplier;

import io.github.zero88.qwe.component.SharedDataLocalProxy;

import lombok.NonNull;

public interface CacheInitializer<R extends CacheInitializer> {

    @NonNull R init(SharedDataLocalProxy context);

    default <T> void addBlockingCache(@NonNull SharedDataLocalProxy context, @NonNull String cacheKey,
                                      @NonNull Supplier<T> blockingCacheProvider) {
        context.getVertx()
               .executeBlocking(future -> future.complete(blockingCacheProvider.get()),
                                result -> context.addData(cacheKey, result.result()));
    }

}

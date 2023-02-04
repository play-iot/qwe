package cloud.playio.qwe.cache;

import java.util.function.Supplier;

import cloud.playio.qwe.SharedDataLocalProxy;

import lombok.NonNull;

public interface CacheInitializer {

    void init(@NonNull SharedDataLocalProxy context);

    default <T> void addBlockingCache(@NonNull SharedDataLocalProxy context, @NonNull String cacheKey,
                                      @NonNull Supplier<T> blockingCacheProvider) {
        context.getVertx()
               .executeBlocking(future -> future.complete(blockingCacheProvider.get()),
                                result -> context.addData(cacheKey, result.result()));
    }

}

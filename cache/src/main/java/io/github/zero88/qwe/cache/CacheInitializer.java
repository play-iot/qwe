package io.github.zero88.qwe.cache;

import lombok.NonNull;

public interface CacheInitializer<R extends CacheInitializer, C> {

    @NonNull R init(@NonNull C context);

}

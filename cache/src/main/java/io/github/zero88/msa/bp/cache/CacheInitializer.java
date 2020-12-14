package io.github.zero88.msa.bp.cache;

import lombok.NonNull;

public interface CacheInitializer<R extends CacheInitializer, C> {

    @NonNull R init(@NonNull C context);

}

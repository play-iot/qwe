package io.github.zero88.msa.bp.cache;

import io.reactivex.Maybe;

import lombok.NonNull;

public interface AsyncLocalCache<K, V> extends LocalCache<K, Maybe<V>> {

    Maybe<V> get(@NonNull K key);

}

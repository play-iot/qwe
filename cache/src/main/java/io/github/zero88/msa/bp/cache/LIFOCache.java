package io.github.zero88.msa.bp.cache;

import lombok.NonNull;

public interface LIFOCache<K, V> extends Cache {

    V first(@NonNull K key);

    V pop(@NonNull K key);

}

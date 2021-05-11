package io.zero88.qwe.cache;

import java.util.Map;
import java.util.Objects;

import lombok.NonNull;

public interface LocalDataCache<K, V> extends LocalCache<K, V> {

    LocalDataCache add(@NonNull K key, V v);

    default LocalDataCache addAll(Map<K, V> all) {
        if (Objects.nonNull(all)) {
            all.forEach(this::add);
        }
        return this;
    }

}

package cloud.playio.qwe.cache;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;

import lombok.NonNull;

public interface AsyncLocalCache<K, V> extends LocalCache<K, Future<@Nullable V>> {

    Future<@Nullable V> get(@NonNull K key);

}

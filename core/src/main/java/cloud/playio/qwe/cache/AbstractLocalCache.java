package cloud.playio.qwe.cache;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.dto.JsonData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter(value = AccessLevel.PROTECTED)
@Accessors(fluent = true)
public abstract class AbstractLocalCache<K, V, C extends AbstractLocalCache> implements LocalCache<K, V>, HasLogger {

    private final ConcurrentMap<K, V> cache = new ConcurrentHashMap<>();
    private Function<K, V> discover;

    @Override
    public V get(@NonNull K key) {
        V val = cache.get(key);
        if (Objects.isNull(discover)) {
            return val;
        }
        if (Objects.nonNull(val)) {
            if (logger().isDebugEnabled()) {
                logger().debug("Get {} by {}:{} from cache", valueLabel(), keyLabel(),
                               key instanceof JsonData ? ((JsonData) key).toJson() : key);
            }
            return val;
        }
        if (logger().isDebugEnabled()) {
            logger().debug("Find {} by {}:{} then put into cache", valueLabel(), keyLabel(),
                           key instanceof JsonData ? ((JsonData) key).toJson() : key);
        }
        return cache.computeIfAbsent(key, discover);
    }

    @Override
    public V remove(@NonNull K key) {
        return cache.remove(key);
    }

    @Override
    public Map<K, V> all() {
        return Collections.unmodifiableMap(cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public C register(Function<K, V> discover) {
        this.discover = discover;
        return (C) this;
    }

    @NonNull
    protected abstract String keyLabel();

    @NonNull
    protected abstract String valueLabel();

}

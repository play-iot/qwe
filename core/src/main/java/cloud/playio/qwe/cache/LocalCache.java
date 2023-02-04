package cloud.playio.qwe.cache;

import java.util.Map;
import java.util.function.Function;

import lombok.NonNull;

public interface LocalCache<K, V> extends Cache {

    V get(@NonNull K key);

    V remove(@NonNull K key);

    Map<K, V> all();

    LocalCache register(Function<K, V> discover);

}

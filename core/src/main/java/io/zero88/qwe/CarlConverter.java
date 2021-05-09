package io.zero88.qwe;

public interface CarlConverter<T, V> {

    V from(T t);

    T to(V v);

    Class<T> fromClass();

    Class<V> toClass();

}

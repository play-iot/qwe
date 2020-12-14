package io.github.zero88.msa.bp.cache;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassGraphCache<K, T> extends AbstractLocalCache<K, Class<T>, ClassGraphCache> {

    private final String keyLabel;

    @Override
    protected String keyLabel() {
        return keyLabel;
    }

    @Override
    protected String valueLabel() {
        return "class";
    }

}

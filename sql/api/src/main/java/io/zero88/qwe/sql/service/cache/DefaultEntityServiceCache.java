package io.zero88.qwe.sql.service.cache;

import java.util.Optional;

import io.zero88.qwe.cache.AbstractLocalCache;
import io.zero88.qwe.cache.LocalDataCache;
import io.zero88.qwe.exceptions.ServiceNotFoundException;
import io.zero88.qwe.sql.EntityMetadata;

import lombok.NonNull;

final class DefaultEntityServiceCache extends AbstractLocalCache<String, String, DefaultEntityServiceCache>
    implements LocalDataCache<String, String>, EntityServiceCacheIndex {

    @Override
    protected @NonNull String keyLabel() {
        return "Entity Metadata";
    }

    @Override
    protected @NonNull String valueLabel() {
        return "Service address";
    }

    @Override
    public DefaultEntityServiceCache add(@NonNull String metadata, @NonNull String serviceAddress) {
        cache().putIfAbsent(metadata, serviceAddress);
        return this;
    }

    @Override
    @NonNull
    public String lookupApiAddress(@NonNull EntityMetadata metadata) {
        return Optional.ofNullable(get(metadata.getClass().getName()))
                       .orElseThrow(() -> new ServiceNotFoundException(
                           "Not found service address of " + metadata.table().getName()));
    }

    @Override
    public EntityServiceCacheIndex add(@NonNull EntityMetadata metadata, @NonNull String serviceAddress) {
        return add(metadata.getClass().getName(), serviceAddress);
    }

}

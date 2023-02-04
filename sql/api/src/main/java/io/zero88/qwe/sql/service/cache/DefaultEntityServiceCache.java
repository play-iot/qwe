package cloud.playio.qwe.sql.service.cache;

import java.util.Optional;

import cloud.playio.qwe.cache.AbstractLocalCache;
import cloud.playio.qwe.cache.LocalDataCache;
import cloud.playio.qwe.exceptions.ServiceNotFoundException;
import cloud.playio.qwe.sql.EntityMetadata;

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

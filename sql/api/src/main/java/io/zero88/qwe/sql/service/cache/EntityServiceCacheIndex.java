package io.zero88.qwe.sql.service.cache;

import io.zero88.qwe.sql.EntityMetadata;

import lombok.NonNull;

public interface EntityServiceCacheIndex extends EntityServiceIndex {

    static EntityServiceCacheIndex create() {
        return new DefaultEntityServiceCache();
    }

    EntityServiceCacheIndex add(@NonNull EntityMetadata metadata, @NonNull String serviceAddress);

}

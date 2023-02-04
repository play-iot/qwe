package cloud.playio.qwe.sql.service.cache;

import cloud.playio.qwe.sql.EntityMetadata;

import lombok.NonNull;

public interface EntityServiceCacheIndex extends EntityServiceIndex {

    static EntityServiceCacheIndex create() {
        return new DefaultEntityServiceCache();
    }

    EntityServiceCacheIndex add(@NonNull EntityMetadata metadata, @NonNull String serviceAddress);

}

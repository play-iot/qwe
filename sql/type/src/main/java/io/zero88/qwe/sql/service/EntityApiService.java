package cloud.playio.qwe.sql.service;

import cloud.playio.qwe.sql.EntityMetadata;

import lombok.NonNull;

public interface EntityApiService {

    @NonNull String prefixServiceName();

    @NonNull
    default String lookupApiName(@NonNull EntityMetadata metadata) {
        return prefixServiceName() + "." + metadata.modelClass().getSimpleName();
    }

}

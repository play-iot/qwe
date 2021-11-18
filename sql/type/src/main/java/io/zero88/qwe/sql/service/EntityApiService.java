package io.zero88.qwe.sql.service;

import io.zero88.qwe.sql.EntityMetadata;

import lombok.NonNull;

public interface EntityApiService {

    @NonNull String prefixServiceName();

    @NonNull
    default String lookupApiName(@NonNull EntityMetadata metadata) {
        return prefixServiceName() + "." + metadata.modelClass().getSimpleName();
    }

}

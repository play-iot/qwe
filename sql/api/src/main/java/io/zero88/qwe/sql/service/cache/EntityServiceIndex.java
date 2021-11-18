package io.zero88.qwe.sql.service.cache;

import io.zero88.qwe.sql.EntityMetadata;

import lombok.NonNull;

public interface EntityServiceIndex {

    String DATA_KEY = "ENTITY_SERVICE_INDEX";

    @NonNull String lookupApiAddress(@NonNull EntityMetadata metadata);

}

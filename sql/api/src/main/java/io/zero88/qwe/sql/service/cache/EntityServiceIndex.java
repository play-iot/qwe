package cloud.playio.qwe.sql.service.cache;

import cloud.playio.qwe.sql.EntityMetadata;

import lombok.NonNull;

public interface EntityServiceIndex {

    String DATA_KEY = "ENTITY_SERVICE_INDEX";

    @NonNull String lookupApiAddress(@NonNull EntityMetadata metadata);

}

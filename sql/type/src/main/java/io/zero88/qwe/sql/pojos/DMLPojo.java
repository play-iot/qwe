package io.zero88.qwe.sql.pojos;

import java.util.Optional;

import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Represents for {@code DML pojo} after executing {@code DML} operation.
 *
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
@Builder(builderClassName = "Builder")
public final class DMLPojo implements JsonRecord {

    /**
     * Request entity
     */
    private final JsonRecord request;
    /**
     * Entity primary key
     */
    private final Object primaryKey;
    /**
     * Database entity
     */
    private final JsonRecord dbEntity;

    public static DMLPojo clone(@NonNull DMLPojo dmlPojo, @NonNull JsonRecord dbEntity) {
        return DMLPojo.builder().request(dmlPojo.request()).primaryKey(dmlPojo.primaryKey()).dbEntity(dbEntity).build();
    }

    @Override
    public JsonRecord fromJson(JsonObject json) {
        return this;
    }

    @Override
    public JsonObject toJson() {
        return Optional.ofNullable(dbEntity).orElse(request).toJson();
    }

}

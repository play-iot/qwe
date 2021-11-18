package io.zero88.qwe.sql.query;

import java.util.Objects;
import java.util.Optional;

import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.sql.EntityMetadata;
import io.vertx.core.Future;
import io.zero88.jooqx.JsonRecord;
import io.reactivex.Single;

import lombok.NonNull;

interface InternalQueryExecutor<P extends JsonRecord> extends EntityQueryExecutor<P> {

    /**
     * Gets entity metadata.
     *
     * @return the metadata
     * @see EntityMetadata
     * @since 1.0.0
     */
    EntityMetadata metadata();

    @Override
    @SuppressWarnings("unchecked")
    default @NonNull Future<P> lookupByPrimaryKey(@NonNull Object primaryKey) {
        return lookupByPrimaryKey(metadata(), primaryKey).flatMap(
            o -> o.map(Single::just).orElse(Single.error(metadata().notFound(primaryKey)))).map(p -> (P) p);
    }

    /**
     * Lookup resource by primary key.
     *
     * @param metadata the metadata
     * @param key      the primary key
     * @return the optional resource in single type
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    default Single<Optional<? extends JsonRecord>> lookupByPrimaryKey(@NonNull EntityMetadata metadata, Object key) {
        return Objects.isNull(key)
               ? Single.just(Optional.empty())
               : (Single<Optional<? extends JsonRecord>>) dao(metadata).findOneById(key);
    }

}

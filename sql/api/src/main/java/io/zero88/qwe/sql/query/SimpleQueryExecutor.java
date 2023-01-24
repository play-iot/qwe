package io.zero88.qwe.sql.query;

import org.jooq.UpdatableRecord;

import io.zero88.qwe.sql.EntityMetadata;
import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.sql.handler.EntityHandler;

import lombok.NonNull;

/**
 * Represents for a {@code sql executor} do {@code DML} or {@code DQL} on {@code simple entity}.
 *
 * @param <P> Type of {@code JsonRecord}
 * @since 1.0.0
 */
public interface SimpleQueryExecutor<P extends JsonRecord> extends EntityQueryExecutor<P> {

    /**
     * Create simple query executor.
     *
     * @param <K>      Type of {@code primary key}
     * @param <P>      Type of {@code JsonRecord}
     * @param <R>      Type of {@code UpdatableRecord}
     * @param <D>      Type of {@code VertxDAO}
     * @param handler  the entity handler
     * @param metadata the metadata
     * @return the simple query executor
     * @see EntityHandler
     * @since 1.0.0
     */
    static <K, P extends JsonRecord, R extends UpdatableRecord<R>, D extends VertxDAO<R, P, K>> SimpleQueryExecutor<P> create(
        @NonNull EntityHandler handler, @NonNull EntityMetadata<K, P, R> metadata) {
        return new SimpleDaoQueryExecutor<>(handler, metadata);
    }

}

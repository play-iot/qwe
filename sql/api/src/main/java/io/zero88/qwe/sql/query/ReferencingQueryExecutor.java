package io.zero88.qwe.sql.query;

import java.util.Optional;

import org.jooq.UpdatableRecord;

import io.zero88.qwe.sql.EntityMetadata;
import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.handler.EntityHandler;
import io.zero88.qwe.sql.marker.ReferencingEntityMarker;

import lombok.NonNull;

/**
 * Represents for a {@code SQL executor} do {@code DML} or {@code DQL} on the {@code database entity} has the
 * relationship to another entity.
 *
 * @param <P> Type of {@code JsonRecord}
 * @since 1.0.0
 */
public interface ReferencingQueryExecutor<P extends JsonRecord> extends SimpleQueryExecutor<P> {

    /**
     * Create reference query executor.
     *
     * @param <K>      Type of {@code primary key}
     * @param <P>      Type of {@code JsonRecord}
     * @param <R>      Type of {@code UpdatableRecord}
     * @param <D>      Type of {@code VertxDAO}
     * @param handler  the entity handler
     * @param metadata the entity metadata
     * @param marker   the reference entity marker
     * @return the reference query executor
     * @see EntityHandler
     * @see EntityMetadata
     * @see ReferencingEntityMarker
     * @since 1.0.0
     */
    static <K, P extends JsonRecord, R extends UpdatableRecord<R>, D extends VertxDAO<R, P, K>> ReferencingQueryExecutor create(
        @NonNull EntityHandler handler, @NonNull EntityMetadata<K, P, R> metadata,
        @NonNull ReferencingEntityMarker marker) {
        return new ReferencingDaoQueryExecutor<>(handler, metadata, marker);
    }

    /**
     * Defines {@code entity marker}.
     *
     * @return the has reference marker
     * @see ReferencingEntityMarker
     * @since 1.0.0
     */
    @NonNull ReferencingEntityMarker marker();

    /**
     * Verify the {@code referenced entities} by {@link ReferencingEntityMarker#referencedEntities()} whether exists or
     * not.
     *
     * @param reqData the request data
     * @return error single if not found any {@code reference entity}, otherwise {@code true} single
     * @since 1.0.0
     */
    default Future<Boolean> checkReferenceExistence(@NonNull RequestData reqData) {
        return marker().referencedEntities()
                       .toObservable()
                       .flatMapSingle(e -> this.findReferenceKey(reqData, e.getKey(), e.getValue())
                                               .map(rk -> this.fetchExists(queryBuilder().exist(e.getKey(), rk))
                                                              .switchIfEmpty(
                                                                  Future.failedFuture(e.getKey().notFound(rk))))
                                               .orElseGet(() -> Future.succeededFuture(true)))
                       .all(aBoolean -> aBoolean);
    }

    default Optional<?> findReferenceKey(@NonNull RequestData reqData, @NonNull EntityMetadata metadata,
                                         @NonNull String refField) {
        final Optional<?> key = metadata.getKey(reqData);
        if (key.isPresent()) {
            return key;
        }
        return Optional.ofNullable(reqData.body().getValue(refField)).map(k -> metadata.parseKey(k.toString()));
    }

}

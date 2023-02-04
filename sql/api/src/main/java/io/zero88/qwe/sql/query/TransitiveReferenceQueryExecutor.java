package cloud.playio.qwe.sql.query;

import org.jooq.UpdatableRecord;

import cloud.playio.qwe.sql.EntityMetadata;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.marker.TransitiveReferenceMarker;

import lombok.NonNull;

/**
 * Represents for a {@code sql executor} do {@code DML} or {@code DQL} on {@code transitive reference entity}.
 *
 * @param <P> Type of {@code JsonRecord}
 * @since 1.0.0
 */
public interface TransitiveReferenceQueryExecutor<P extends JsonRecord> extends ReferencingQueryExecutor<P> {

    /**
     * Create transitive reference query executor.
     *
     * @param <K>      Type of {@code primary key}
     * @param <P>      Type of {@code JsonRecord}
     * @param <R>      Type of {@code UpdatableRecord}
     * @param <D>      Type of {@code VertxDAO}
     * @param handler  the entity handler
     * @param metadata the metadata
     * @param marker   the transitive marker
     * @return the transitive reference query executor
     * @since 1.0.0
     */
    static <K, P extends JsonRecord, R extends UpdatableRecord<R>, D extends VertxDAO<R, P, K>> TransitiveReferenceQueryExecutor<P> create(
        @NonNull EntityHandler handler, @NonNull EntityMetadata<K, P, R> metadata,
        @NonNull TransitiveReferenceMarker marker) {
        return new TransitiveReferenceDaoQueryExecutor<>(handler, metadata, marker);
    }

    /**
     * @return transitive reference marker
     * @see TransitiveReferenceMarker
     * @since 1.0.0
     */
    @Override
    @NonNull TransitiveReferenceMarker marker();

}

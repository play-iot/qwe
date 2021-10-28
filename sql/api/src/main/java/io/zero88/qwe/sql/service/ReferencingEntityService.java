package io.zero88.qwe.sql.service;

import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.marker.ReferencingEntityMarker;
import io.zero88.qwe.sql.query.ReferencingQueryExecutor;
import io.zero88.qwe.sql.service.transformer.ReferencingEntityTransformer;

import lombok.NonNull;

/**
 * Represents an entity service that holds a {@code resource entity} contains one or more {@code reference} to other
 * resources.
 * <p>
 * It means the {@code service context resource} has an {@code one-to-one} or {@code many-to-one} relationship to
 * another resource.
 * <p>
 * In mapping to {@code database term}, the current {@code table} has the {@code foreign key} to another {@code table}.
 *
 * @param <P> Type of {@code JsonRecord}
 * @param <M> Type of {@code EntityMetadata}
 * @see EntityService
 * @see ReferencingEntityMarker
 * @since 1.0.0
 */
public interface ReferencingEntityService<P extends JsonRecord, M extends EntityMetadata>
    extends SimpleEntityService<P, M>, ReferencingEntityMarker {

    /**
     * @return reference query executor
     * @see ReferencingQueryExecutor
     */
    @Override
    @SuppressWarnings("unchecked")
    default @NonNull ReferencingQueryExecutor<P> queryExecutor() {
        return ReferencingQueryExecutor.create(entityHandler(), context(), this);
    }

    /**
     * @return reference entity transformer
     * @see ReferencingEntityTransformer
     */
    @Override
    @NonNull ReferencingEntityTransformer transformer();

}

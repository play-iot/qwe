package io.zero88.qwe.sql.service;

import io.zero88.qwe.sql.CompositeMetadata;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.marker.GroupReferencingEntityMarker;
import io.zero88.qwe.sql.pojos.CompositePojo;
import io.zero88.qwe.sql.query.GroupQueryExecutor;
import io.zero88.qwe.sql.service.transformer.GroupEntityTransformer;

import lombok.NonNull;

/**
 * Represents for an entity service that holds a {@code Group resource}.
 *
 * @param <M>  Type of {@code EntityMetadata}
 * @param <CP> Type of {@code CompositePojo}
 * @param <CM> Type of {@code CompositeMetadata}
 * @see CompositePojo
 * @see CompositeMetadata
 * @see EntityService
 * @see GroupReferencingEntityMarker
 * @since 1.0.0
 */
public interface GroupEntityService<M extends EntityMetadata, CP extends CompositePojo, CM extends CompositeMetadata>
    extends SimpleEntityService<CP, CM>, GroupReferencingEntityMarker {

    /**
     * Declares group context metadata.
     *
     * @return group context metadata
     * @see CompositeMetadata
     * @since 1.0.0
     */
    @Override
    @NonNull CM context();

    /**
     * Declares raw context metadata.
     *
     * @return raw context metadata
     * @see EntityMetadata
     * @since 1.0.0
     */
    @NonNull M rawContext();

    /**
     * Declares group query executor.
     *
     * @return group query executor
     * @see GroupQueryExecutor
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("unchecked")
    default @NonNull GroupQueryExecutor<CP> queryExecutor() {
        return GroupQueryExecutor.create(entityHandler(), rawContext(), context(), this);
    }

    /**
     * Declares group entity transformer.
     *
     * @return group entity transformer
     * @see GroupEntityTransformer
     * @since 1.0.0
     */
    @NonNull GroupEntityTransformer transformer();

}

package cloud.playio.qwe.sql.service;

import cloud.playio.qwe.sql.CompositeMetadata;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.marker.GroupReferencingEntityMarker;
import cloud.playio.qwe.sql.pojos.CompositePojo;
import cloud.playio.qwe.sql.query.GroupQueryExecutor;
import cloud.playio.qwe.sql.service.decorator.GroupRequestDecorator;
import cloud.playio.qwe.sql.service.transformer.GroupEntityTransformer;

import lombok.NonNull;

/**
 * Abstract service to implement {@code CRUD} listeners for the {@code Group entity}.
 *
 * @param <M>  Type of {@code EntityMetadata}
 * @param <CP> Type of {@code CompositePojo}
 * @param <CM> Type of {@code CompositeMetadata}
 * @see EntityMetadata
 * @see CompositePojo
 * @see CompositeMetadata
 * @see GroupEntityService
 * @see GroupRequestDecorator
 * @see GroupEntityTransformer
 * @since 1.0.0
 */
public abstract class AbstractGroupEntityService<M extends EntityMetadata, CP extends CompositePojo,
                                                    CM extends CompositeMetadata>
    extends AbstractReferencingEntityService<CP, CM>
    implements GroupEntityService<M, CP, CM>, GroupRequestDecorator, GroupEntityTransformer {

    /**
     * Instantiates a new Abstract group entity service.
     *
     * @param entityHandler the entity handler
     * @since 1.0.0
     */
    public AbstractGroupEntityService(@NonNull EntityHandler entityHandler) {
        super(entityHandler);
    }

    @Override
    public @NonNull GroupQueryExecutor<CP> queryExecutor() {
        return GroupEntityService.super.queryExecutor();
    }

    @Override
    public @NonNull GroupEntityTransformer transformer() {
        return this;
    }

    @Override
    public GroupReferencingEntityMarker marker() {
        return this;
    }

}

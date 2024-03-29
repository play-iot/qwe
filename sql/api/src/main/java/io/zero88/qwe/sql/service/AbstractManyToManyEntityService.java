package cloud.playio.qwe.sql.service;

import io.vertx.core.Future;
import cloud.playio.qwe.sql.CompositeMetadata;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.marker.ManyToManyMarker;
import cloud.playio.qwe.sql.pojos.CompositePojo;
import cloud.playio.qwe.sql.query.ComplexQueryExecutor;
import cloud.playio.qwe.sql.service.decorator.ManyToManyRequestDecorator;
import cloud.playio.qwe.sql.service.transformer.ManyToManyEntityTransformer;
import cloud.playio.qwe.sql.validation.CompositeValidation;
import cloud.playio.qwe.sql.validation.OperationValidator;

import lombok.NonNull;

/**
 * Abstract service to implement {@code CRUD} listeners for the {@code database entity} has a {@code many-to-many}
 * relationship.
 *
 * @param <P> Type of {@code CompositePojo}
 * @param <M> Type of {@code CompositeMetadata}
 * @see ManyToManyEntityService
 * @see ManyToManyRequestDecorator
 * @see ManyToManyEntityTransformer
 * @since 1.0.0
 */
public abstract class AbstractManyToManyEntityService<P extends CompositePojo, M extends CompositeMetadata>
    extends AbstractEntityService<P, M>
    implements ManyToManyEntityService<P, M>, ManyToManyRequestDecorator, ManyToManyEntityTransformer {

    /**
     * Instantiates a new Abstract many to many entity service.
     *
     * @param entityHandler the entity handler
     * @since 1.0.0
     */
    public AbstractManyToManyEntityService(@NonNull EntityHandler entityHandler) {
        super(entityHandler);
    }

    @Override
    public abstract M context();

    @Override
    public @NonNull ComplexQueryExecutor<P> queryExecutor() {
        return ManyToManyEntityService.super.queryExecutor();
    }

    @Override
    public @NonNull CompositeValidation validation() { return this.context(); }

    @Override
    public @NonNull ManyToManyEntityTransformer transformer() { return this; }

    @Override
    public @NonNull EntityMetadata resourceMetadata() {
        return resource();
    }

    @Override
    protected OperationValidator initCreationValidator() {
        return OperationValidator.create((req, pojo) -> Future.succeededFuture(context().onCreating(req)));
    }

    @Override
    public ManyToManyMarker marker() { return this; }

}

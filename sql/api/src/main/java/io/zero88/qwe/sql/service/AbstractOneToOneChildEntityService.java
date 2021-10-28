package io.zero88.qwe.sql.service;

import java.util.Collection;

import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.handler.EntityHandler;
import io.zero88.qwe.sql.service.decorator.HasReferenceRequestDecorator;

import lombok.NonNull;

/**
 * Abstract service to implement {@code CRUD} listeners for the {@code database entity} has a {@code one-to-one}
 * relationship, and is {@code child} role.
 *
 * @param <P> Type of {@code JsonRecord}
 * @param <M> Type of {@code EntityMetadata}
 * @since 1.0.0
 */
public abstract class AbstractOneToOneChildEntityService<P extends JsonRecord, M extends EntityMetadata>
    extends AbstractReferencingEntityService<P, M>
    implements HasReferenceRequestDecorator, OneToOneChildEntityService<P, M> {

    /**
     * Instantiates a new Abstract one to many entity service.
     *
     * @param entityHandler the entity handler
     * @since 1.0.0
     */
    public AbstractOneToOneChildEntityService(@NonNull EntityHandler entityHandler) {
        super(entityHandler);
    }

    @Override
    public @NonNull Collection<EventAction> getAvailableEvents() {
        return OneToOneChildEntityService.super.getAvailableEvents();
    }

}

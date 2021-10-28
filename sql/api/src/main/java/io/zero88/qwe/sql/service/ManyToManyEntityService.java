package io.zero88.qwe.sql.service;

import io.zero88.qwe.sql.CompositeMetadata;
import io.zero88.qwe.sql.marker.ManyToManyMarker;
import io.zero88.qwe.sql.pojos.CompositePojo;
import io.zero88.qwe.sql.query.ComplexQueryExecutor;
import io.zero88.qwe.sql.service.transformer.ManyToManyEntityTransformer;
import io.zero88.qwe.sql.validation.CompositeValidation;

import lombok.NonNull;

/**
 * Represents an entity service that holds a {@code middlemen resource} has one or more {@code reference} to other
 * resources.
 * <p>
 * It presents {@code many-to-one} connection between {@code middlemen resource} with {@code reference resource}. But
 * between among {@code reference resources}, it is {@code many-to-many} relationship
 *
 * @param <CP> Type of {@code CompositePojo}
 * @param <CM> Type of {@code CompositeMetadata}
 * @see ManyToManyMarker
 * @since 1.0.0
 */
public interface ManyToManyEntityService<CP extends CompositePojo, CM extends CompositeMetadata>
    extends SimpleEntityService<CP, CM>, ManyToManyMarker {

    /**
     * @apiNote It represents for a joining table in {@code many-to-many} relationship
     */
    @Override
    @NonNull CM context();

    /**
     * Composite validation
     *
     * @return composite validation
     * @see CompositeValidation
     */
    @Override
    @NonNull CompositeValidation validation();

    /**
     * @return many-to-many entity transformer
     * @see ManyToManyEntityTransformer
     */
    @Override
    @NonNull ManyToManyEntityTransformer transformer();

    /**
     * @return complex query executor
     * @see ComplexQueryExecutor
     */
    @Override
    @SuppressWarnings("unchecked")
    default @NonNull ComplexQueryExecutor<CP> queryExecutor() {
        return entityHandler().complexQuery()
                              .from(context())
                              .context(reference())
                              .with(resource())
                              .references(transformer().marker().referencedEntities());
    }

}

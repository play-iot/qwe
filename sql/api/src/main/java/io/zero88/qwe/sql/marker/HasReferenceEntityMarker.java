package io.zero88.qwe.sql.marker;

import lombok.NonNull;

/**
 * Represents for an {@code database entity} marker.
 * <p>
 * It manifests the {@code resource entity} has one or more {@code reference key(s)} to other resources.
 *
 * @since 1.0.0
 */
public interface HasReferenceEntityMarker extends EntityMarker {

    /**
     * Defines the {@code referenced entities} of this {@code resource entity}
     *
     * @return the referenced entities
     * @apiNote The {@code referenced entity} is an entity that attached into the {@code resource entity} via {@code
     *     foreign key}
     * @see EntityReferences
     * @since 1.0.0
     */
    @NonNull EntityReferences referencedEntities();

}

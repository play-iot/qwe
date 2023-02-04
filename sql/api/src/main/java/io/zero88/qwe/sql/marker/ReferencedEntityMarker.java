package cloud.playio.qwe.sql.marker;

import lombok.NonNull;

/**
 * Represents for an {@code database entity} marker.
 * <p>
 * It manifests {@code resource entity} is referenced by other resources.
 * <p>
 * In mapping to {@code database term}, it is known as {@code primary key} of this {@code table} is {@code foreign key}
 * in other {@code tables}.
 *
 * @since 1.0.0
 */
public interface ReferencedEntityMarker extends EntityMarker {

    /**
     * Defines the {@code dependant entities} a.k.a {@code referencing entities} of this {@code resource entity}
     *
     * @return dependant entities
     * @see EntityReferences
     * @since 1.0.0
     */
    @NonNull EntityReferences dependantEntities();

}

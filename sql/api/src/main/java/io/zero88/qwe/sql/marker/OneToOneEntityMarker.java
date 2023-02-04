package cloud.playio.qwe.sql.marker;

import lombok.NonNull;

/**
 * Represents for an {@code database entity} marker.
 * <p>
 * It manifests {@code resource entity} is referenced by other resources with {@code one-to-one} relationship.
 *
 * @since 1.0.0
 */
public interface OneToOneEntityMarker extends ReferencedEntityMarker, ReferencingEntityMarker {

    /**
     * Defines the {@code dependant entities} in {@code one-to-one} relationship of this {@code resource entity}
     *
     * @return dependant entities
     * @see EntityReferences
     * @since 1.0.0
     */
    @NonNull EntityReferences dependantEntities();

    /**
     * @implNote By API default, the referenced entities will equivalent the dependant entities. But in real-world,
     *     almost case does not, so remember to override it.
     */
    @Override
    default @NonNull EntityReferences referencedEntities() {
        return dependantEntities();
    }

}

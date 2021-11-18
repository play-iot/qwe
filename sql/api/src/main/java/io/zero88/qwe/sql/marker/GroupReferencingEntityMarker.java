package io.zero88.qwe.sql.marker;

import java.util.Set;

/**
 * Represents for an {@code database entity} marker.
 * <p>
 * It manifests one {@code resource entity} has one or more {@code references} to other resources and at the same time,
 * it includes these {@code referenced entity} into itself
 *
 * @see ReferencingEntityMarker
 * @since 1.0.0
 */
public interface GroupReferencingEntityMarker extends ReferencingEntityMarker {

    /**
     * Declares {@code group references} for references entities.
     *
     * @return the entity references
     * @since 1.0.0
     */
    EntityReferences groupReferences();

    @Override
    default Set<String> ignoreFields() {
        return groupReferences().ignoreFields();
    }

}

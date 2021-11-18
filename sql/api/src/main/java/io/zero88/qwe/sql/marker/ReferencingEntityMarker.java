package io.zero88.qwe.sql.marker;

import java.util.Set;

import lombok.NonNull;

/**
 * Represents for an {@code database entity} marker.
 * <p>
 * It manifests {@code resource entity} has one or more {@code reference} to other resources.
 * <p>
 * In mapping to {@code database term}, it is known as current {@code table} has the {@code foreign keys} to other
 * {@code tables}.
 * <p>
 * A marker declares a list of {@code reference fields} that are used for computing {@code request data} to filter
 * exactly request key then make {@code SQL query}
 *
 * @since 1.0.0
 */
public interface ReferencingEntityMarker extends HasReferenceEntityMarker {

    /**
     * Defines ignore fields in {@code response} based on {@code request data} and {@code reference fields}
     *
     * @return ignore fields
     * @since 1.0.0
     */
    @NonNull
    default Set<String> ignoreFields() {
        return referencedEntities().ignoreFields();
    }

}

package io.zero88.qwe.sql.marker;

import java.util.Map;

import io.zero88.qwe.sql.EntityMetadata;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents for an {@code database entity} marker.
 * <p>
 * {@code Transitive reference entity} represents for a case:
 * <ul>
 *  <li>Table A has a <i>reference field</i> to Table B</li>
 *  <li>Table B has a <i>reference field</i> to Table C</li>
 *  <li>Table A has the <b>transitive reference</b> to Table C</li>
 * </ul>
 *
 * @see ReferencingEntityMarker
 * @since 1.0.0
 */
public interface TransitiveReferenceMarker extends ReferencingEntityMarker {

    /**
     * Declares transitive references mapping.
     * <p>
     * Each mapping key is one of entity metadata in {@link #referencedEntities()}, and a corresponding mapping value is
     * transitive reference of this metadata
     *
     * @return transitive references
     * @see EntityMetadata
     * @see TransitiveEntity
     * @since 1.0.0
     */
    @NonNull Map<EntityMetadata, TransitiveEntity> transitiveReferences();

    /**
     * Represents for Transitive entity.
     *
     * @since 1.0.0
     */
    @Getter
    @RequiredArgsConstructor
    class TransitiveEntity {

        /**
         * Defines {@code search entity context}
         *
         * @since 1.0.0
         */
        @NonNull
        private final EntityMetadata context;
        /**
         * Defines entity references
         *
         * @see EntityReferences
         * @since 1.0.0
         */
        @NonNull
        private final EntityReferences references;

    }

}

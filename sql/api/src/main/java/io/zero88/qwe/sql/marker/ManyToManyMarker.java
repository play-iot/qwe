package cloud.playio.qwe.sql.marker;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cloud.playio.qwe.sql.CompositeMetadata;
import cloud.playio.qwe.sql.EntityMetadata;

import lombok.NonNull;

/**
 * Represents for an {@code database entity} marker.
 * <p>
 * It manifests a relationship between more than one {@code database entities}, in-which both sides can relate to
 * multiple instances of the other side
 *
 * @since 1.0.0
 */
public interface ManyToManyMarker extends HasReferenceEntityMarker {

    /**
     * Declares physical database entity
     *
     * @return physical entity metadata
     * @apiNote It represents for a joining table in {@code many-to-many} relationship
     * @since 1.0.0
     */
    @NonNull CompositeMetadata context();

    /**
     * Declares logical database entity
     *
     * @return logical entity metadata
     * @apiNote Represents one reference table in {@code many-to-many} relationship that is actual {@code service
     *     resource context}
     * @since 1.0.0
     */
    @NonNull EntityMetadata reference();

    /**
     * Declares logical database entities
     *
     * @return logical entities metadata
     * @apiNote Represents list of reference tables in {@code many-to-many} relationship that is actual {@code
     *     service resource context}
     * @since 1.0.0
     */
    default @NonNull List<EntityMetadata> references() {
        return Collections.singletonList(reference());
    }

    /**
     * Declares service presentation resource
     *
     * @return presentation entity metadata
     * @apiNote Represents one reference table in {@code many-to-many} relationship that is {@code service resource
     *     presentation}
     * @since 1.0.0
     */
    @NonNull EntityMetadata resource();

    default @NonNull EntityReferences referencedEntities() {
        final EntityReferences entityReferences = new EntityReferences();
        references().forEach(entityReferences::add);
        return entityReferences.add(resource());
    }

    @Override
    default Set<String> ignoreFields() {
        return Stream.concat(Stream.of(context().requestKeyName(), resource().requestKeyName()),
                             references().stream().map(EntityMetadata::requestKeyName)).collect(Collectors.toSet());
    }

}

package cloud.playio.qwe.sql.service.transformer;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.marker.GroupReferencingEntityMarker;

import lombok.NonNull;

/**
 * The interface Group entity transformer.
 *
 * @see ReferencingEntityTransformer
 * @since 1.0.0
 */
public interface GroupEntityTransformer extends ReferencingEntityTransformer {

    /**
     * @return group reference marker
     * @see GroupReferencingEntityMarker
     * @since 1.0.0
     */
    GroupReferencingEntityMarker marker();

    /**
     * Ignore fields that includes {@code audit field}, {@code reference field} and {@code group field}
     *
     * @param requestData request data
     * @return ignore fields
     */
    @Override
    default Set<String> ignoreFields(@NonNull RequestData requestData) {
        return Stream.of(ReferencingEntityTransformer.super.ignoreFields(requestData), marker().ignoreFields())
                     .flatMap(Collection::stream)
                     .collect(Collectors.toSet());
    }

    /**
     * Same as {@link #ignoreFields(RequestData)} but without {@code group field}
     *
     * @param requestData request data
     * @return ignore fields
     * @since 1.0.0
     */
    default Set<String> showGroupFields(@NonNull RequestData requestData) {
        return ReferencingEntityTransformer.super.ignoreFields(requestData);
    }

}

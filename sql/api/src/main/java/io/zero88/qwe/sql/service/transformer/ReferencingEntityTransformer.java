package cloud.playio.qwe.sql.service.transformer;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.sql.marker.ReferencingEntityMarker;

import lombok.NonNull;

/**
 * Represents for Reference entity transformer.
 *
 * @see EntityTransformer
 * @since 1.0.0
 */
public interface ReferencingEntityTransformer extends EntityTransformer {

    /**
     * Declares {@code has reference} marker.
     *
     * @return the referencing marker
     * @see ReferencingEntityMarker
     * @since 1.0.0
     */
    ReferencingEntityMarker marker();

    @Override
    default Set<String> ignoreFields(@NonNull RequestData requestData) {
        final RequestFilter filter = requestData.filter();
        return Stream.concat(EntityTransformer.super.ignoreFields(requestData).stream(),
                             marker().ignoreFields().stream().filter(s -> filter.fieldNames().contains(s)))
                     .filter(s -> !resourceMetadata().jsonKeyName().equals(s) &&
                                  !resourceMetadata().requestKeyName().equals(s))
                     .map(String::toLowerCase)
                     .collect(Collectors.toSet());
    }

}

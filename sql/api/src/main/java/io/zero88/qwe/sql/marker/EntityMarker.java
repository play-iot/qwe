package io.zero88.qwe.sql.marker;

import java.util.Collections;
import java.util.Set;

/**
 * Represents for an {@code database entity} marker.
 *
 * @since 1.0.0
 */
public interface EntityMarker {

    /**
     * Declares default {@code  ignore fields}.
     *
     * @return ignore fields
     * @since 1.0.0
     */
    default Set<String> ignoreFields() {
        return Collections.emptySet();
    }

}

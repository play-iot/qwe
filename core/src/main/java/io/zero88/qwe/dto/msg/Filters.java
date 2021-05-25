package io.zero88.qwe.dto.msg;

import java.util.Arrays;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Represents for Filters.
 *
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Filters {

    /**
     * The constant SYSTEM_SIGN.
     */
    public static final String SYSTEM_SIGN = "_";

    /**
     * For {@code advance query}
     *
     * @since 1.0.0
     */
    public static final String QUERY = "_q";

    /**
     * To {@code prettify} response
     *
     * @since 1.0.0
     */
    public static final String PRETTY = "_pretty";

    /**
     * For {@code language}
     *
     * @since 1.0.0
     */
    public static final String LANG = "_lang";

    /**
     * For {@code pagination}
     *
     * @since 1.0.0
     */
    public static final String PAGE = "_page";

    /**
     * For {@code pagination}
     *
     * @since 1.0.0
     */
    public static final String PER_PAGE = "_per_page";

    /**
     * For {@code audit}
     *
     * @since 1.0.0
     */
    public static final String AUDIT = "_audit";

    /**
     * For {@code audit} in temporary
     *
     * @since 1.0.0
     */
    public static final String TEMP_AUDIT = "_temp_audit";

    /**
     * For {@code sort}
     *
     * @since 1.0.0
     */
    public static final String SORT = "_sort";

    /**
     * For {@code include}
     *
     * @since 1.0.0
     */
    public static final String INCLUDE = "_incl";

    /**
     * For {@code force}
     *
     * @since 1.0.0
     */
    public static final String FORCE = "_force";

    public static final List<String> BOOLEAN_PARAMS = Arrays.asList(PRETTY, AUDIT, TEMP_AUDIT, FORCE);

}

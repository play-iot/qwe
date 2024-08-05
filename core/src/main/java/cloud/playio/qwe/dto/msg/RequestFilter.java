package cloud.playio.qwe.dto.msg;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.JsonData;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents for Request filter.
 *
 * @since 1.0.0
 */
@NoArgsConstructor
public final class RequestFilter extends JsonObject implements JsonData {

    public static final Collector<Entry<String, Object>, RequestFilter, RequestFilter> COLLECTOR = Collector.of(
        RequestFilter::new, (f, entry) -> f.put(entry.getKey(), entry.getValue()),
        (f1, f2) -> (RequestFilter) f1.mergeIn(f2), Characteristics.CONCURRENT, Characteristics.UNORDERED,
        Characteristics.IDENTITY_FINISH);

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
    public static final String PAGE_SIZE = "_page_size";
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

    /**
     * Instantiates a new Request filter.
     *
     * @param filter the filter
     * @since 1.0.0
     */
    @JsonCreator
    public RequestFilter(JsonObject filter) {
        super(filter.copy().getMap());
    }

    /**
     * Is pretty.
     *
     * @return the boolean
     * @see RequestFilter#PRETTY
     * @since 1.0.0
     */
    public boolean isPretty() {
        return parseBoolean(PRETTY);
    }

    /**
     * Has force.
     *
     * @return the boolean
     * @see RequestFilter#FORCE
     * @since 1.0.0
     */
    public boolean hasForce() {
        return parseBoolean(FORCE);
    }

    /**
     * Has audit.
     *
     * @return the boolean
     * @see RequestFilter#AUDIT
     * @since 1.0.0
     */
    public boolean hasAudit() {
        return parseBoolean(AUDIT);
    }

    /**
     * Has temp audit.
     *
     * @return the boolean
     * @see RequestFilter#TEMP_AUDIT
     * @since 1.0.0
     */
    public boolean hasTempAudit() {
        return parseBoolean(TEMP_AUDIT);
    }

    /**
     * Get advance query.
     *
     * @return the advance query
     * @see RequestFilter#QUERY
     * @since 1.0.0
     */
    public String advanceQuery() {
        return getString(QUERY);
    }

    public RequestFilter put(String key, Object value) {
        return (RequestFilter) super.put(key, value);
    }

    /**
     * Gets includes.
     *
     * @return the includes
     * @since 1.0.0
     */
    public Set<String> getIncludes() {
        return Arrays.stream(getString(INCLUDE, "").split(",")).collect(Collectors.toSet());
    }

    /**
     * Gets system filter by filter system identifier sign
     *
     * @return the system filter
     * @see RequestFilter#SYSTEM_SIGN
     * @since 1.0.0
     */
    public RequestFilter getSystemFilter() {
        return make(this, entry -> entry.getKey().startsWith(SYSTEM_SIGN)).collect(COLLECTOR);
    }

    /**
     * Gets application filter.
     *
     * @return the application filter
     * @since 1.0.0
     */
    public RequestFilter getAppFilter() {
        return make(this, entry -> !entry.getKey().startsWith(SYSTEM_SIGN)).collect(COLLECTOR);
    }

    private static Stream<Entry<String, Object>> make(JsonObject json,
                                                      @NonNull Predicate<Entry<String, Object>> predicate) {
        return json.stream().filter(predicate);
    }

    private boolean parseBoolean(String pretty) {
        return Boolean.parseBoolean(Strings.toString(getValue(pretty)));
    }

}

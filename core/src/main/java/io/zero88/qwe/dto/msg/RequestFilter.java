package io.zero88.qwe.dto.msg;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.zero88.qwe.dto.JsonData;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;

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
     * Instantiates a new Request filter.
     *
     * @param filter the filter
     * @since 1.0.0
     */
    public RequestFilter(JsonObject filter) {
        super(filter.copy().getMap());
    }

    /**
     * Is pretty.
     *
     * @return the boolean
     * @see Filters#PRETTY
     * @since 1.0.0
     */
    public boolean isPretty() {
        return parseBoolean(Filters.PRETTY);
    }

    /**
     * Has force.
     *
     * @return the boolean
     * @see Filters#FORCE
     * @since 1.0.0
     */
    public boolean hasForce() {
        return parseBoolean(Filters.FORCE);
    }

    /**
     * Has audit.
     *
     * @return the boolean
     * @see Filters#AUDIT
     * @since 1.0.0
     */
    public boolean hasAudit() {
        return parseBoolean(Filters.AUDIT);
    }

    /**
     * Has temp audit.
     *
     * @return the boolean
     * @see Filters#TEMP_AUDIT
     * @since 1.0.0
     */
    public boolean hasTempAudit() {
        return parseBoolean(Filters.TEMP_AUDIT);
    }

    /**
     * Get advance query.
     *
     * @return the advance query
     * @see Filters#QUERY
     * @since 1.0.0
     */
    public String advanceQuery() {
        return getString(Filters.QUERY);
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
        return Arrays.stream(getString(Filters.INCLUDE, "").split(",")).collect(Collectors.toSet());
    }

    /**
     * Gets system filter by filter system identifier sign
     *
     * @return the system filter
     * @see Filters#SYSTEM_SIGN
     * @since 1.0.0
     */
    public RequestFilter getSystemFilter() {
        return make(entry -> entry.getKey().startsWith(Filters.SYSTEM_SIGN)).collect(COLLECTOR);
    }

    /**
     * Gets extra filter.
     *
     * @return the extra filter
     * @since 1.0.0
     */
    public RequestFilter getExtraFilter() {
        return make(entry -> !entry.getKey().startsWith(Filters.SYSTEM_SIGN)).collect(COLLECTOR);
    }

    public Stream<Entry<String, Object>> streamExtraFilter() {
        return make(entry -> !entry.getKey().startsWith(Filters.SYSTEM_SIGN));
    }

    private Stream<Entry<String, Object>> make(@NonNull Predicate<Entry<String, Object>> predicate) {
        return this.stream().filter(predicate);
    }

    private boolean parseBoolean(String pretty) {
        return Boolean.parseBoolean(Strings.toString(this.getValue(pretty)));
    }

}

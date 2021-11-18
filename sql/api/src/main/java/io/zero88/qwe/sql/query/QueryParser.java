package io.zero88.qwe.sql.query;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;

import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

/**
 * Represents Query parser helper.
 *
 * @since 1.0.0
 */
public interface QueryParser {

    /**
     * From reference json object.
     *
     * @param reference the reference
     * @param root      the root
     * @return the json object
     * @since 1.0.0
     */
    static RequestFilter fromReference(@NonNull EntityMetadata reference, RequestFilter root) {
        if (Objects.isNull(root)) {
            return new RequestFilter();
        }
        return streamRefs(reference, root).collect(RequestFilter::new,
                                                   (json, entry) -> json.put(entry.getKey(), entry.getValue()),
                                                   (json1, json2) -> json1.mergeIn(json2, true));
    }

    /**
     * Ref key entry with root entry.
     *
     * @param s the s
     * @return the entry
     * @since 1.0.0
     */
    static Entry<String, String> refKeyEntryWithRoot(String s) {
        return new SimpleEntry<>(s.substring(0, s.indexOf('.')), s);
    }

    /**
     * Find ref entry.
     *
     * @param filter the filter
     * @param entry  the entry
     * @return the entry
     * @since 1.0.0
     */
    static Entry<String, Object> findRef(@NonNull JsonObject filter, Entry<String, String> entry) {
        return new SimpleEntry<>(entry.getValue().replaceAll("^" + entry.getKey() + "\\.", ""),
                                 filter.getValue(entry.getValue()));
    }

    /**
     * Stream refs stream.
     *
     * @param reference the reference
     * @param root      the root
     * @return the stream
     * @since 1.0.0
     */
    static Stream<Entry<String, Object>> streamRefs(@NonNull EntityMetadata reference, @NonNull JsonObject root) {
        return root.fieldNames()
                   .stream()
                   .filter(s -> s.contains("."))
                   .filter(s -> reference.singularKeyName().equals(s.substring(0, s.indexOf('.'))))
                   .map(QueryParser::refKeyEntryWithRoot)
                   .map(entry -> findRef(root, entry));
    }

}

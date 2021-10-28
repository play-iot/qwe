package io.zero88.qwe.sql.tables;

import java.util.Map;
import java.util.Map.Entry;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import lombok.NonNull;

/**
 * This keeps information about json fields that map with database column
 *
 * @param <R> Type of {@code parameter}
 * @see Table
 * @since 1.0.0
 */
public interface JsonTable<R extends Record> extends Table<R> {

    /**
     * Declares Json field map with Database field.
     *
     * @return the map
     * @since 1.0.0
     */
    Map<String, String> jsonFields();

    /**
     * Gets Database field by given json name.
     *
     * @param jsonName the json name
     * @return the Database field
     * @since 1.0.0
     */
    default Field getField(@NonNull String jsonName) {
        return field(jsonFields().getOrDefault(jsonName, jsonName));
    }

    /**
     * Gets json field by given Database field.
     *
     * @param field the database field
     * @return the json field
     * @since 1.0.0
     */
    default String getJsonField(@NonNull Field field) {
        return jsonFields().entrySet()
                           .stream()
                           .filter(entry -> entry.getValue().equals(field.getName()))
                           .findFirst()
                           .map(Entry::getKey)
                           .orElse(null);
    }

}

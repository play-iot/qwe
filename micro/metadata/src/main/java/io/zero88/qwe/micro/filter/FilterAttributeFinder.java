package io.zero88.qwe.micro.filter;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.json.JsonObject;

public interface FilterAttributeFinder<T> {

    @NotNull String attribute();

    /**
     * Find an attribute from given filter
     *
     * @param filter the filter
     * @return attribute
     */
    @Nullable T findAttribute(JsonObject filter);

    static Optional<String> findString(JsonObject filter, String attribute) {
        return Optional.ofNullable(filter).flatMap(f -> Optional.ofNullable(f.getString(attribute)));
    }

    interface FilterStringFinder extends FilterAttributeFinder<String> {

        @Override
        default @Nullable String findAttribute(JsonObject filter) {
            return findString(filter, attribute()).orElse(null);
        }

    }

}

package io.zero88.qwe.micro.filter;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.micro.filter.RecordPredicateFactory.SearchFlag;

import lombok.NonNull;

/**
 * Master record predicate
 *
 * @see Record
 */
public interface RecordPredicateFactory extends BiFunction<JsonObject, SearchFlag, Predicate<Record>> {

    enum SearchFlag {
        ONE, MANY;

        boolean isOne() {
            return this == ONE;
        }

        boolean isMany() {
            return this == MANY;
        }
    }

    /**
     * Build predicate from given request action and filter
     *
     * @param filter     the filter
     * @param searchFlag search flag
     * @return the record predicate
     */
    @NonNull Predicate<Record> apply(@NonNull JsonObject filter, @NonNull SearchFlag searchFlag);

}

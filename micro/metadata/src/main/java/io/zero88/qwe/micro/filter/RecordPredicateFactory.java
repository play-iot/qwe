package io.zero88.qwe.micro.filter;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.event.EventAction;

import lombok.NonNull;

/**
 * Master record predicate
 *
 * @see Record
 */
public interface RecordPredicateFactory extends BiFunction<EventAction, JsonObject, Predicate<Record>> {

    /**
     * Create service record filter
     *
     * @param filter Given filter parameters
     * @param action Given query action: {@link EventAction#GET_LIST} or {@link EventAction#GET_ONE}
     * @return filter function
     */
    static @NonNull Function<Record, Boolean> filter(@NonNull JsonObject filter, @NonNull EventAction action) {
        return PredicateFactoryLoader.instance()
                                     .getPredicatesFactories()
                                     .stream()
                                     .map(p -> p.apply(action, filter))
                                     .reduce(Predicate::and)
                                     .orElseGet(() -> r -> false)::test;
    }

    /**
     * Build predicate from given request action and filter
     *
     * @param action request action
     * @param filter the filter
     * @return the record predicate
     */
    @Override
    @NonNull Predicate<Record> apply(@NonNull EventAction action, @NonNull JsonObject filter);

}

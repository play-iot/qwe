package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.micro.filter.FilterAttributeFinder.FilterStringFinder;

import lombok.NonNull;

/**
 * Represents for predicate that is built from {@code identifier} and {@code indicator} in the runtime filter
 * <p>
 * For example:
 * <pre>
 * /find?identifier=sample.name&_by=name
 * /find?identifier=sample&_by=group
 * /find?identifier=uuid&_by=registration
 * /find?identifier="/test/123"&_by=path
 * </pre>
 *
 * @see RecordPredicateFactory
 * @see ServiceLocatorParams#BY
 * @see ServiceLocatorParams#IDENTIFIER
 */
public interface ByPredicateFactory extends RecordPredicateFactory, FilterStringFinder {

    String DEFAULT_INDICATOR = "REGISTRATION";

    @Override
    default @NonNull Predicate<Record> apply(@NonNull EventAction action, @NonNull JsonObject filter) {
        final String identifier = findAttribute(filter);
        if (Strings.isBlank(identifier)) {
            if (action == EventAction.GET_ONE) {
                throw new IllegalArgumentException("Missing record identifier");
            }
            return r -> true;
        }
        if (!by().equalsIgnoreCase(findIndicator(filter))) {
            return r -> true;
        }
        return apply(action, identifier);
    }

    @Override
    default @NotNull String attribute() {
        return ServiceLocatorParams.IDENTIFIER;
    }

    /**
     * By which indicator
     *
     * @return the indicator name
     */
    String by();

    /**
     * Build record predicate
     *
     * @param action     request action
     * @param identifier given identifier
     * @return a record predicate
     */
    Predicate<Record> apply(EventAction action, String identifier);

    default String findIndicator(JsonObject filter) {
        return FilterAttributeFinder.findString(filter, ServiceLocatorParams.BY).orElse(DEFAULT_INDICATOR);
    }

}

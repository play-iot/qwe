package io.zero88.qwe.micro.filter;

import java.util.Objects;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.event.EventAction;

/**
 * Simple predicate for one attribute
 *
 * @param <T> Type of attributes
 */
public interface SimplePredicateFactory<T> extends RecordPredicateFactory, FilterAttributeFinder<T> {

    default Predicate<Record> apply(EventAction action, JsonObject filter) {
        final T attribute = findAttribute(filter);
        return Objects.isNull(attribute) ? r -> true : apply(attribute);
    }

    Predicate<Record> apply(@NotNull T attribute);

}

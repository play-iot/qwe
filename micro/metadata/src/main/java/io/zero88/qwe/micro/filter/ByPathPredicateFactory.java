package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import io.vertx.servicediscovery.Record;
import io.zero88.qwe.event.EventAction;

import lombok.NonNull;

public interface ByPathPredicateFactory<T> extends ByPredicateFactory {

    @Override
    default String by() {
        return "LOCATION";
    }

    @Override
    default Predicate<Record> apply(EventAction action, String identifier) {
        return testTypePredicate().and(record -> testLocation(parseLocation(record), identifier));
    }

    default Predicate<Record> testTypePredicate() {
        return record -> serviceType().equals(record.getType());
    }

    /**
     * Declares service type
     * @return service type
     */
    @NonNull String serviceType();

    @Nullable T parseLocation(Record record);

    boolean testLocation(T location, String identifier);

}

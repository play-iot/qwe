package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;

public interface ByPathPredicateFactory<T> extends ByPredicateFactory {

    String INDICATOR = "location";

    @Override
    default String by() {
        return INDICATOR;
    }

    @Override
    default Predicate<Record> apply(String identifier, SearchFlag searchFlag, JsonObject filter) {
        return typePredicate().and(record -> testLocation(parseLocation(record), identifier, filter));
    }

    default Predicate<Record> typePredicate() {
        return record -> serviceType().equals(record.getType());
    }

    /**
     * Declares service type
     * @return service type
     */
    @NonNull String serviceType();

    @Nullable T parseLocation(Record record);

    boolean testLocation(T location, String identifier, JsonObject filter);

}

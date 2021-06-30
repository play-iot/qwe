package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.micro.HasServiceType;

public interface ByPathPredicateFactory<T> extends ByPredicateFactory, HasServiceType {

    @Override
    default String by() {
        return BY_PATH;
    }

    @Override
    default Predicate<Record> apply(String identifier, SearchFlag searchFlag, JsonObject filter) {
        return record -> !ServiceTypePredicateFactory.testType(record, serviceType()) ||
                         testLocation(parseLocation(record), identifier, filter);
    }

    @Nullable T parseLocation(Record record);

    boolean testLocation(T location, String identifier, JsonObject filter);

}

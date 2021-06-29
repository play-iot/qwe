package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import io.vertx.servicediscovery.Record;
import io.zero88.qwe.micro.filter.FilterAttributeFinder.FilterStringFinder;

public final class ServiceTypePredicateFactory
    implements SimplePredicateFactory<String>, FilterStringFinder, DefaultPredicateFactory {

    @Override
    public @NotNull String attribute() {
        return ServiceFilterParam.TYPE;
    }

    @Override
    public Predicate<Record> apply(String type) {
        return record -> record.getType().equals(type);
    }

}

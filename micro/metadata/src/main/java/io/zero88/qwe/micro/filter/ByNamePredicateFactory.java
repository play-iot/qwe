package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

public final class ByNamePredicateFactory implements ByPredicateFactory, DefaultPredicateFactory {

    public static final String INDICATOR = "name";

    @Override
    public String by() {
        return INDICATOR;
    }

    @Override
    public Predicate<Record> apply(String name, SearchFlag searchFlag, JsonObject filter) {
        if (searchFlag.isMany()) {
            return r -> r.getName().toLowerCase().contains(name.toLowerCase());
        }
        return r -> r.getName().equalsIgnoreCase(name.toLowerCase());
    }

}

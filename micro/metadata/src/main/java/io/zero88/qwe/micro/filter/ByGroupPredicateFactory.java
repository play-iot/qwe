package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

public final class ByGroupPredicateFactory implements ByPredicateFactory, DefaultPredicateFactory {

    @Override
    public String by() {
        return "group";
    }

    @Override
    public Predicate<Record> apply(String identifier, SearchFlag searchFlag, JsonObject filter) {
        if (searchFlag.isMany()) {
            return r -> r.getName().toLowerCase().startsWith(identifier.toLowerCase());
        }
        return r -> {
            final int idx = r.getName().lastIndexOf(".");
            return idx != -1 && r.getName().substring(0, idx).equalsIgnoreCase(identifier.toLowerCase());
        };
    }

}

package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import io.vertx.servicediscovery.Record;
import io.zero88.qwe.event.EventAction;

public final class ByGroupPredicateFactory implements ByPredicateFactory, DefaultPredicateFactory {

    @Override
    public String by() {
        return "GROUP";
    }

    @Override
    public Predicate<Record> apply(EventAction action, String identifier) {
        if (action == EventAction.GET_LIST) {
            return r -> r.getName().toLowerCase().startsWith(identifier.toLowerCase());
        }
        if (action == EventAction.GET_ONE) {
            return r -> {
                final int idx = r.getName().lastIndexOf(".");
                return idx != -1 && r.getName().substring(0, idx).equalsIgnoreCase(identifier.toLowerCase());
            };
        }
        return r -> true;
    }

}

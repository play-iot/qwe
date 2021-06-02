package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import io.vertx.servicediscovery.Record;
import io.zero88.qwe.event.EventAction;

public final class ByNamePredicateFactory implements ByPredicateFactory, DefaultPredicateFactory {

    @Override
    public String by() {
        return "NAME";
    }

    @Override
    public Predicate<Record> apply(EventAction action, String name) {
        if (action == EventAction.GET_LIST) {
            return r -> r.getName().toLowerCase().contains(name.toLowerCase());
        }
        if (action == EventAction.GET_ONE) {
            return r -> r.getName().equalsIgnoreCase(name.toLowerCase());
        }
        return r -> true;
    }

}

package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import io.vertx.servicediscovery.Record;
import io.zero88.qwe.event.EventAction;

public final class ByRegistrationPredicateFactory implements ByPredicateFactory, DefaultPredicateFactory {

    @Override
    public String by() {
        return DEFAULT_INDICATOR;
    }

    @Override
    public Predicate<Record> apply(EventAction action, String registration) {
        return record -> registration.equalsIgnoreCase(record.getRegistration());
    }

}

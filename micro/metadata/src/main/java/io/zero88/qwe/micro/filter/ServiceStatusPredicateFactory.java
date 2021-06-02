package io.zero88.qwe.micro.filter;

import java.util.Locale;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;

public final class ServiceStatusPredicateFactory implements SimplePredicateFactory<Status>, DefaultPredicateFactory {

    @Override
    public @NotNull String attribute() {
        return ServiceLocatorParams.STATUS;
    }

    @Override
    public Status findAttribute(JsonObject filter) {
        return FilterAttributeFinder.findString(filter, attribute())
                                    .map(s -> s.toUpperCase(Locale.ROOT))
                                    .map(Status::valueOf)
                                    .orElse(Status.UP);
    }

    @Override
    public Predicate<Record> apply(Status status) {
        return r -> r.getStatus() == status;
    }

}

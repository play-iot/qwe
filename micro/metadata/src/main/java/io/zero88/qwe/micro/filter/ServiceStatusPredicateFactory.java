package io.zero88.qwe.micro.filter;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;
import io.zero88.qwe.utils.JsonUtils;

public final class ServiceStatusPredicateFactory implements SimplePredicateFactory<Status> {

    /**
     * Define special case
     */
    public static String ALL = "all";

    @Override
    public @NotNull String attribute() {
        return ServiceFilterParam.STATUS;
    }

    @Override
    public Status findAttribute(JsonObject filter) {
        final Optional<String> statusOpt = JsonUtils.findString(filter, attribute());
        if (!statusOpt.isPresent()) {
            return Status.UP;
        }
        return statusOpt.map(s -> s.toUpperCase(Locale.ROOT))
                        .filter(s -> !ALL.equalsIgnoreCase(s))
                        .map(Status::valueOf)
                        .orElse(null);
    }

    @Override
    public Predicate<Record> apply(Status status) {
        return r -> r.getStatus() == status;
    }

}

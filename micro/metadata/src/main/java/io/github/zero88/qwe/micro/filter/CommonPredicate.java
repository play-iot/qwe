package io.github.zero88.qwe.micro.filter;

import java.util.Objects;
import java.util.function.Predicate;

import io.github.zero88.qwe.micro.type.ServiceScope;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;

import lombok.NonNull;

final class CommonPredicate implements RecordPredicate {

    @Override
    public @NonNull Predicate<Record> apply(@NonNull JsonObject filter) {
        final String statusParam = filter.getString(ServiceLocatorParams.STATUS);
        Status status = Objects.isNull(statusParam)
                        ? Status.UP
                        : Functions.getIfThrow(() -> Status.valueOf(statusParam.toUpperCase())).orElse(Status.UP);
        ServiceScope scope = ServiceScope.parse(filter.getString(ServiceLocatorParams.SCOPE));
        return byScope(scope, filter.getString(ServiceLocatorParams.TYPE)).and(r -> r.getStatus().equals(status));
    }

    private Predicate<Record> byScope(@NonNull ServiceScope scope, final String type) {
        if (Strings.isNotBlank(type)) {
            return record -> record.getType().equals(type);
        }
        return scope.getPredicate();
    }

}

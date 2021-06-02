package io.zero88.qwe.micro.filter;

import java.util.Objects;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

/**
 * @see ServiceScope
 */
public final class ServiceScopePredicateFactory
    implements SimplePredicateFactory<ServiceScope>, DefaultPredicateFactory {

    @Override
    public @NotNull String attribute() {
        return ServiceLocatorParams.SCOPE;
    }

    @Override
    public ServiceScope findAttribute(JsonObject filter) {
        return FilterAttributeFinder.findString(filter, attribute()).map(ServiceScope::parse).orElse(null);
    }

    @Override
    public Predicate<Record> apply(ServiceScope scope) {
        return Objects.isNull(scope) ? r -> true : scope.getPredicate();
    }

}

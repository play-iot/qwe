package io.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.micro.filter.FilterAttributeFinder.FilterStringFinder;
import io.zero88.qwe.utils.JsonUtils;

public final class ServiceNamePredicateFactory
    implements SimplePredicateFactory<String>, FilterStringFinder, ByPredicateFactory {

    public Predicate<Record> apply(JsonObject filter, SearchFlag searchFlag) {
        if (filter.containsKey(ServiceFilterParam.BY)) {
            return ByPredicateFactory.super.apply(filter, searchFlag);
        }
        return SimplePredicateFactory.super.apply(filter, searchFlag);
    }

    @Override
    public @Nullable String findAttribute(JsonObject filter) {
        return JsonUtils.findString(filter, filter.containsKey(ServiceFilterParam.BY)
                                            ? ByPredicateFactory.super.attribute()
                                            : this.attribute()).orElse(null);
    }

    @Override
    public String by() {
        return ByPredicateFactory.BY_NAME;
    }

    @Override
    public @NotNull String attribute() {
        return ServiceFilterParam.NAME;
    }

    @Override
    public Predicate<Record> apply(String identifier, SearchFlag searchFlag, JsonObject filter) {
        return r -> r.getName().equalsIgnoreCase(identifier);
    }

    @Override
    public Predicate<Record> apply(@NotNull String name) {
        return r -> r.getName().equalsIgnoreCase(name);
    }

}

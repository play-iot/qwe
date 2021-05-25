package io.zero88.qwe.micro.filter;

import java.util.function.Function;
import java.util.function.Predicate;

import io.zero88.qwe.event.EventAction;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;

/**
 * Master record predicate
 */
public interface RecordPredicate extends Function<JsonObject, Predicate<Record>> {

    /**
     * Create service record filter
     *
     * @param filter Given filter parameters
     * @param action Given query action: {@link EventAction#GET_LIST} or {@link EventAction#GET_ONE}
     * @return filter function
     */
    static @NonNull Function<Record, Boolean> filter(@NonNull JsonObject filter, @NonNull EventAction action) {
        JsonObject advFilter = new JsonObject().put(ServiceLocatorParams.TYPE, filter.remove(ServiceLocatorParams.TYPE))
                                               .put(ServiceLocatorParams.STATUS, filter.remove(
                                                   ServiceLocatorParams.STATUS))
                                               .put(ServiceLocatorParams.SCOPE, filter.remove(
                                                   ServiceLocatorParams.SCOPE))
                                               .put(ServiceLocatorParams.IDENTIFIER, filter.remove(
                                                   ServiceLocatorParams.IDENTIFIER))
                                               .put(ServiceLocatorParams.BY, filter.remove(ServiceLocatorParams.BY));
        return new CommonPredicate().apply(advFilter)
                                    .and(new IdentifierPredicate(action).apply(advFilter))
                                    .and(r -> MetadataPredicate.apply(r, filter))
                                    .and(r -> r.match(filter))::test;
    }

    @Override
    @NonNull Predicate<Record> apply(@NonNull JsonObject filter);

}

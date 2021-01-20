package io.github.zero88.qwe.micro.filter;

import java.util.function.Function;
import java.util.function.Predicate;

import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.micro.ServiceGatewayIndex.Params;
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
        JsonObject advancedFilter = new JsonObject().put(Params.TYPE, filter.remove(Params.TYPE))
                                                    .put(Params.STATUS, filter.remove(Params.STATUS))
                                                    .put(Params.SCOPE, filter.remove(Params.SCOPE))
                                                    .put(Params.IDENTIFIER, filter.remove(Params.IDENTIFIER))
                                                    .put(Params.BY, filter.remove(Params.BY));
        return new CommonPredicate().apply(advancedFilter)
                                    .and(new IdentifierPredicate(action).apply(advancedFilter))
                                    .and(r -> MetadataPredicate.apply(r, filter))
                                    .and(r -> r.match(filter))::test;
    }

    @Override
    @NonNull Predicate<Record> apply(@NonNull JsonObject filter);

}

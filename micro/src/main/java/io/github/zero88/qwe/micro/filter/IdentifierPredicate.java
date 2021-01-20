package io.github.zero88.qwe.micro.filter;

import java.util.function.Predicate;

import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.micro.ServiceGatewayIndex.Params;
import io.github.zero88.qwe.micro.filter.ByPredicate.ByPredicateEnum;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class IdentifierPredicate implements RecordPredicate {

    private final EventAction action;

    @Override
    public @NonNull Predicate<Record> apply(@NonNull JsonObject filter) {
        String identifier = filter.getString(Params.IDENTIFIER);
        if (Strings.isBlank(identifier)) {
            return r -> true;
        }
        return ByPredicateEnum.parse(action, filter.getString(Params.BY)).by(identifier);
    }

}

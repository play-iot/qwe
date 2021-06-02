package io.zero88.qwe.micro.filter;

import java.util.Objects;
import java.util.function.Predicate;

import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.micro.servicetype.EventMessageHttpService;

import lombok.NonNull;

public final class EventMessageServicePathPredicate
    implements ByPathPredicateFactory<EventMethodDefinition>, DefaultPredicateFactory {

    @Override
    public String serviceType() {
        return EventMessageHttpService.TYPE;
    }

    @Override
    public @NonNull Predicate<Record> apply(@NonNull EventAction action, @NonNull JsonObject filter) {
        final String identifier = findAttribute(filter);
        if (Strings.isBlank(identifier) || !by().equalsIgnoreCase(findIndicator(filter))) {
            return r -> true;
        }
        return testTypePredicate().and(record -> testLocation(record, identifier, filter));
    }

    @Override
    public EventMethodDefinition parseLocation(Record record) {
        return EventMethodDefinition.from(record.getLocation());
    }

    @Override
    public boolean testLocation(EventMethodDefinition definition, String path) {
        return definition.getServicePath().equals(Urls.combinePath(path));
    }

    private boolean testLocation(Record record, String identifier, JsonObject filter) {
        final EventMethodDefinition definition = Objects.requireNonNull(parseLocation(record));
        final EventAction locationAction = FilterAttributeFinder.findString(filter, ServiceLocatorParams.ACTION)
                                                                .map(EventAction::parse)
                                                                .orElse(EventAction.UNKNOWN);
        return testLocation(definition, identifier) &&
               definition.getMapping().stream().anyMatch(map -> map.getAction().equals(locationAction));
    }

}

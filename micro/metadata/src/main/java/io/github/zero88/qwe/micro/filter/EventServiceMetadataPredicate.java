package io.github.zero88.qwe.micro.filter;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.micro.http.EventMethodDefinition;
import io.github.zero88.qwe.micro.servicetype.EventMessageService;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;

class EventServiceMetadataPredicate implements MetadataPredicate {

    @Override
    public boolean test(@NonNull Record record, @NonNull JsonObject filter) {
        final String action = Strings.toString(filter.remove(ServiceLocatorParams.ACTION));
        if (Strings.isBlank(action)) {
            return true;
        }
        EventAction event = EventAction.parse(action);
        EventMethodDefinition definition = JsonData.convert(
            record.getMetadata().getJsonObject(EventMessageService.EVENT_METHOD_CONFIG), EventMethodDefinition.class);
        return definition.getMapping().stream().anyMatch(map -> map.getAction().equals(event));
    }

}

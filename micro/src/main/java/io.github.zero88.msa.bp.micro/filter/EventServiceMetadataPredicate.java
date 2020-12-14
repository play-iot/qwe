package io.github.zero88.msa.bp.micro.filter;

import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.http.event.EventMethodDefinition;
import io.github.zero88.msa.bp.micro.ServiceGatewayIndex.Params;
import io.github.zero88.msa.bp.micro.type.EventMessageService;

import lombok.NonNull;

class EventServiceMetadataPredicate implements MetadataPredicate {

    @Override
    public boolean test(@NonNull Record record, @NonNull JsonObject filter) {
        final String action = Strings.toString(filter.remove(Params.ACTION));
        if (Strings.isBlank(action)) {
            return true;
        }
        EventAction event = EventAction.parse(action);
        EventMethodDefinition definition = JsonData.convert(
            record.getMetadata().getJsonObject(EventMessageService.EVENT_METHOD_CONFIG), EventMethodDefinition.class);
        return definition.getMapping().stream().anyMatch(map -> map.getAction().equals(event));
    }

}

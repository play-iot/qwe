package io.zero88.qwe.micro.transfomer;

import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.EventMethodMapping;
import io.zero88.qwe.micro.servicetype.EventMessageService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;

class EventServiceRecordTransformer implements RecordTransformer {

    @Override
    public @NonNull RecordOutput transform(@NonNull Record record) {
        EventMethodDefinition definition = JsonData.convert(
            record.getMetadata().getJsonObject(EventMessageService.EVENT_METHOD_CONFIG), EventMethodDefinition.class);
        final JsonArray paths = definition.getMapping()
                                          .stream()
                                          .map(this::serializeEventMethod)
                                          .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
        return RecordOutput.builder()
                           .name(record.getName())
                           .status(record.getStatus())
                           .location(record.getLocation().getString(Record.ENDPOINT))
                           .endpoints(paths)
                           .build();
    }

    protected JsonObject serializeEventMethod(@NonNull EventMethodMapping map) {
        return new JsonObject().put("method", map.method()).put("path", map.getCapturePath());
    }

}

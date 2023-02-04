package cloud.playio.qwe.micro.transfomer;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import cloud.playio.qwe.http.EventMethodDefinition;
import cloud.playio.qwe.http.EventMethodMapping;
import cloud.playio.qwe.micro.servicetype.EventMessageHttpService;
import cloud.playio.qwe.utils.JsonUtils.JsonCollectors;

import lombok.NonNull;

public class EventServicePublicView implements RecordTransformer {

    @Override
    public @NonNull String serviceType() {
        return EventMessageHttpService.TYPE;
    }

    @Override
    public @NonNull RecordTransformer.ViewType viewType() {
        return ViewType.END_USER;
    }

    @Override
    public @NonNull RecordOutput transform(@NonNull Record record) {
        final JsonArray paths = EventMethodDefinition.from(record.getLocation())
                                                     .getMapping()
                                                     .stream()
                                                     .map(this::serializeEventMethod)
                                                     .collect(JsonCollectors.toArray());
        return RecordOutput.builder()
                           .name(record.getName())
                           .status(record.getStatus())
                           .endpoint(record.getLocation().getString(Record.ENDPOINT))
                           .paths(paths)
                           .build();
    }

    protected JsonObject serializeEventMethod(@NonNull EventMethodMapping map) {
        return new JsonObject().put("method", map.method()).put("path", map.getCapturePath());
    }

}

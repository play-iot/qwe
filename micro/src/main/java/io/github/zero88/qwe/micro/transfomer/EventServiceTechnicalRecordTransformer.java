package io.github.zero88.qwe.micro.transfomer;

import io.github.zero88.qwe.micro.metadata.EventMethodMapping;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

class EventServiceTechnicalRecordTransformer extends EventServiceRecordTransformer implements RecordTransformer {

    @Override
    protected JsonObject serializeEventMethod(@NonNull EventMethodMapping map) {
        return map.toJson();
    }

}

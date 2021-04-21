package io.zero88.qwe.micro.transfomer;

import io.zero88.qwe.micro.http.EventMethodMapping;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

class EventServiceTechnicalRecordTransformer extends EventServiceRecordTransformer implements RecordTransformer {

    @Override
    protected JsonObject serializeEventMethod(@NonNull EventMethodMapping map) {
        return map.toJson();
    }

}

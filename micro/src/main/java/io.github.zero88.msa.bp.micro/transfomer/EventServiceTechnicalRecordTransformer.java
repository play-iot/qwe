package io.github.zero88.msa.bp.micro.transfomer;

import io.github.zero88.msa.bp.micro.metadata.EventMethodMapping;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

class EventServiceTechnicalRecordTransformer extends EventServiceRecordTransformer implements RecordTransformer {

    @Override
    protected JsonObject serializeEventMethod(@NonNull EventMethodMapping map) {
        return map.toJson();
    }

}

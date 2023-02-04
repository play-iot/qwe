package cloud.playio.qwe.micro.transfomer;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.http.EventMethodMapping;

import lombok.NonNull;

public final class EventServiceTechnicalView extends EventServicePublicView implements RecordTransformer {

    @Override
    public @NonNull RecordTransformer.ViewType viewType() {
        return ViewType.TECHNICAL;
    }

    @Override
    protected JsonObject serializeEventMethod(@NonNull EventMethodMapping map) {
        return map.toJson();
    }

}

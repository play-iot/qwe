package io.zero88.qwe.micro.transfomer;

import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.HttpLocation;

import lombok.NonNull;

public final class HttpEndpointRecordView implements RecordTransformer {

    @Override
    public @NonNull String serviceType() {
        return HttpEndpoint.TYPE;
    }

    @Override
    public @NonNull RecordTransformer.ViewType viewType() {
        return ViewType.END_USER;
    }

    @Override
    public @NonNull RecordOutput transform(@NonNull Record record) {
        HttpLocation location = new HttpLocation(record.getLocation());
        return RecordOutput.builder()
                           .name(record.getName())
                           .type(record.getType())
                           .status(record.getStatus())
                           .endpoint(location.getEndpoint())
                           .build();
    }

}

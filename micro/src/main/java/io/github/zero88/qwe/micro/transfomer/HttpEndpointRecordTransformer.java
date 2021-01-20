package io.github.zero88.qwe.micro.transfomer;

import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpLocation;

import lombok.NonNull;

class HttpEndpointRecordTransformer implements RecordTransformer {

    @Override
    public @NonNull RecordOutput transform(@NonNull Record record) {
        HttpLocation location = new HttpLocation(record.getLocation());
        return RecordOutput.builder()
                           .name(record.getName())
                           .type(record.getType())
                           .status(record.getStatus())
                           .location(location.getEndpoint())
                           .build();
    }

}

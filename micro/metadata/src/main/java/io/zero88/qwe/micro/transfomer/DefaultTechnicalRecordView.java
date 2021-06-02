package io.zero88.qwe.micro.transfomer;

import io.vertx.servicediscovery.Record;

import lombok.NonNull;

public final class DefaultTechnicalRecordView implements TechnicalRecordView {

    @Override
    public @NonNull RecordOutput transform(@NonNull Record record) {
        return RecordOutput.builder()
                           .registration(record.getRegistration())
                           .name(record.getName())
                           .type(record.getType())
                           .status(record.getStatus())
                           .location(record.getLocation())
                           .metadata(record.getMetadata())
                           .build();
    }

}

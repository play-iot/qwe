package io.zero88.qwe.micro.transfomer;

import io.vertx.servicediscovery.Record;

import lombok.NonNull;

public final class DefaultPublicRecordView implements PublicRecordView {

    @Override
    public @NonNull RecordOutput transform(@NonNull Record record) {
        return RecordOutput.builder()
                           .name(record.getName())
                           .type(record.getType())
                           .status(record.getStatus())
                           .endpoint(record.getLocation().getString(Record.ENDPOINT))
                           .build();
    }

}

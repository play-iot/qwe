package io.github.zero88.qwe.micro.transfomer;

import io.github.zero88.qwe.micro.servicetype.EventMessageService;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;

class DetailRecordTransformer implements RecordTransformer {

    @Override
    public @NonNull RecordOutput transform(@NonNull Record record) {
        if (EventMessageService.TYPE.equals(record.getType())) {
            return new EventServiceTechnicalRecordTransformer().transform(record);
        }
        return RecordOutput.builder()
                           .registration(record.getRegistration())
                           .name(record.getName())
                           .type(record.getType())
                           .status(record.getStatus())
                           .location(record.getLocation().getString(Record.ENDPOINT))
                           .metadata(record.getMetadata())
                           .build();
    }

}

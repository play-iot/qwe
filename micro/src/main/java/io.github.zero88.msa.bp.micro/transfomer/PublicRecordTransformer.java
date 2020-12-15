package io.github.zero88.msa.bp.micro.transfomer;

import io.github.zero88.msa.bp.micro.type.EventMessageService;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;

import lombok.NonNull;

class PublicRecordTransformer implements RecordTransformer {

    @Override
    public @NonNull RecordOutput transform(@NonNull Record record) {
        if (EventMessageService.TYPE.equals(record.getType())) {
            return new EventServiceRecordTransformer().transform(record);
        }
        if (HttpEndpoint.TYPE.equals(record.getType())) {
            return new HttpEndpointRecordTransformer().transform(record);
        }
        return RecordOutput.builder()
                           .name(record.getName())
                           .type(record.getType())
                           .status(record.getStatus())
                           .location(record.getLocation().getString(Record.ENDPOINT))
                           .build();
    }

}

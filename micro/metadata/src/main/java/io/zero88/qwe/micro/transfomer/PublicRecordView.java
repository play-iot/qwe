package io.zero88.qwe.micro.transfomer;

import io.vertx.servicediscovery.spi.ServiceType;

import lombok.NonNull;

public interface PublicRecordView extends RecordTransformer {

    @Override
    default @NonNull String serviceType() {
        return ServiceType.UNKNOWN;
    }

    @Override
    default @NonNull RecordTransformer.ViewType viewType() {
        return ViewType.END_USER;
    }

}

package cloud.playio.qwe.micro.transfomer;

import io.vertx.servicediscovery.spi.ServiceType;

import lombok.NonNull;

public interface TechnicalRecordView extends RecordTransformer {

    @Override
    default @NonNull String serviceType() {
        return ServiceType.UNKNOWN;
    }

    @Override
    default @NonNull RecordTransformer.ViewType viewType() {
        return ViewType.TECHNICAL;
    }

}

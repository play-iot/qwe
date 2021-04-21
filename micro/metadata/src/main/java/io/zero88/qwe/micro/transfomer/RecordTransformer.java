package io.zero88.qwe.micro.transfomer;

import io.vertx.servicediscovery.Record;

import lombok.NonNull;

public interface RecordTransformer {

    static RecordTransformer create(@NonNull RecordView view) {
        if (view == RecordView.END_USER) {
            return new PublicRecordTransformer();
        }
        return new DetailRecordTransformer();
    }

    @NonNull RecordOutput transform(@NonNull Record record);

    enum RecordView {

        END_USER, TECHNICAL;

        public static RecordView parse(String view) {
            if (TECHNICAL.name().equalsIgnoreCase(view)) {
                return TECHNICAL;
            }
            return END_USER;
        }
    }

}

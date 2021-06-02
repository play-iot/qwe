package io.zero88.qwe.micro.transfomer;

import io.vertx.servicediscovery.Record;

import lombok.NonNull;

/**
 * Transform {@link Record} to response output
 */
public interface RecordTransformer {

    @NonNull String serviceType();

    @NonNull ViewType viewType();

    @NonNull RecordOutput transform(@NonNull Record record);

    enum ViewType {

        END_USER, TECHNICAL;

        public static ViewType parse(String view) {
            if (TECHNICAL.name().equalsIgnoreCase(view)) {
                return TECHNICAL;
            }
            return END_USER;
        }
    }

}

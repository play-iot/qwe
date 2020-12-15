package io.github.zero88.msa.bp.micro.filter;

import io.github.zero88.msa.bp.micro.type.EventMessageService;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.spi.ServiceType;
import io.vertx.servicediscovery.types.HttpEndpoint;

import lombok.NonNull;

/**
 * Represents a detail particular filter in corresponding metadata by each type of service record
 *
 * @see ServiceType
 * @see HttpEndpoint
 * @see EventMessageService
 */
public interface MetadataPredicate {

    static boolean apply(@NonNull Record record, @NonNull JsonObject filter) {
        if (EventMessageService.TYPE.equals(record.getType())) {
            return new EventServiceMetadataPredicate().test(record, filter);
        }
        return true;
    }

    boolean test(@NonNull Record record, @NonNull JsonObject filter);

}

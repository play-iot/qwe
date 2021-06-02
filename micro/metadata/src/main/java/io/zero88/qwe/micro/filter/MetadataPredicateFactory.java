package io.zero88.qwe.micro.filter;

import java.util.Objects;
import java.util.function.Predicate;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.event.EventAction;

import lombok.NonNull;

/**
 * Represents a detail particular filter in corresponding metadata by each type of service record
 */
public interface MetadataPredicateFactory extends SimplePredicateFactory<JsonObject> {

    @Override
    default @NonNull Predicate<Record> apply(@NonNull EventAction action, @NonNull JsonObject filter) {
        return r -> Objects.isNull(r.getMetadata()) || test(r.getMetadata(), action, filter);
    }

    @Override
    default JsonObject findAttribute(JsonObject filter) {
        return filter;
    }

    boolean test(@NonNull JsonObject recordMetadata, @NonNull EventAction action, @NonNull JsonObject filter);

}

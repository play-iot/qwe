package io.zero88.qwe.micro.filter;

import java.util.Objects;
import java.util.function.Predicate;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;

/**
 * Represents a detail particular filter in corresponding metadata by each type of service record
 */
public interface MetadataPredicateFactory extends SimplePredicateFactory<JsonObject> {

    @Override
    default @NonNull Predicate<Record> apply(@NonNull JsonObject filter, SearchFlag searchFlag) {
        return r -> Objects.isNull(r.getMetadata()) || test(r.getMetadata(), searchFlag, filter);
    }

    @Override
    default JsonObject findAttribute(JsonObject filter) {
        return filter;
    }

    boolean test(@NonNull JsonObject recordMetadata, @NonNull SearchFlag searchFlag, @NonNull JsonObject filter);

}

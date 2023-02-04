package cloud.playio.qwe.sql.service.decorator;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import io.github.zero88.utils.Functions;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.marker.HasReferenceEntityMarker;

import lombok.NonNull;

public interface HasReferenceRequestDecorator extends RequestDecorator {

    static JsonObject convertKey(@NonNull RequestData reqData, @NonNull Stream<Entry<EntityMetadata, String>> ref) {
        JsonObject body = reqData.body();
        JsonObject extra = new JsonObject();
        ref.filter(entry -> !body.containsKey(entry.getKey().singularKeyName()) && body.containsKey(entry.getValue()))
           .forEach(entry -> extra.put(entry.getKey().singularKeyName(),
                                       new JsonObject().put(entry.getKey().jsonKeyName(),
                                                            body.getValue(entry.getValue()))));
        return extra;
    }

    static JsonObject convertKey(@NonNull RequestData reqData, @NonNull EntityMetadata context,
                                 EntityMetadata... metadata) {
        return convertKey(reqData, context, Arrays.asList(metadata));
    }

    static JsonObject convertKey(@NonNull RequestData reqData, @NonNull EntityMetadata context,
                                 @NonNull List<EntityMetadata> metadata) {
        JsonObject object = new JsonObject();
        Function<EntityMetadata, Object> valFunc = meta -> JsonData.checkAndConvert(meta.parseKey(reqData));
        Function<EntityMetadata, String> keyFunc = meta -> context.requestKeyName().equals(meta.requestKeyName())
                                                           ? context.jsonKeyName()
                                                           : meta.requestKeyName();
        metadata.stream()
                .filter(Objects::nonNull)
                .map(meta -> new SimpleEntry<>(keyFunc.apply(meta),
                                               Functions.getIfThrow(() -> valFunc.apply(meta)).orElse(null)))
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .forEach(entry -> object.put(entry.getKey(), entry.getValue()));
        return object;
    }

    @NonNull EntityMetadata context();

    @NonNull HasReferenceEntityMarker marker();

    @Override
    default @NonNull RequestData onCreatingOneResource(@NonNull RequestData requestData) {
        return recomputeRequestData(requestData, convertKey(requestData, marker().referencedEntities().stream()));
    }

    @Override
    @NonNull
    default RequestData onModifyingOneResource(@NonNull RequestData requestData) {
        final JsonObject extra = convertKey(requestData, context(), context());
        final JsonObject refExtra = convertKey(requestData, marker().referencedEntities().stream());
        return recomputeRequestData(requestData, extra.mergeIn(refExtra, true));
    }

    @Override
    @NonNull
    default RequestData onDeletingOneResource(@NonNull RequestData requestData) {
        return onModifyingOneResource(requestData);
    }

    @Override
    @NonNull
    default RequestData onReadingManyResource(@NonNull RequestData requestData) {
        return recomputeRequestData(requestData, null);
    }

    @Override
    @NonNull
    default RequestData onReadingOneResource(@NonNull RequestData requestData) {
        return recomputeRequestData(requestData, convertKey(requestData, context(), context()));
    }

    default RequestData recomputeRequestData(@NonNull RequestData requestData, JsonObject extra) {
        JsonObject body = Optional.ofNullable(requestData.body()).orElseGet(JsonObject::new);
        JsonObject filter = new JsonObject(marker().referencedEntities().computeRequest(body));
        Optional.ofNullable(extra).ifPresent(e -> filter.mergeIn(e, true));
        body = body.mergeIn(filter, true);
        final JsonObject combineFilter = requestData.filter().mergeIn(filter, true);
        return RequestData.builder()
                          .body(body)
                          .headers(requestData.headers())
                          .filter(combineFilter)
                          .sort(requestData.sort())
                          .pagination(requestData.pagination())
                          .build();
    }

}

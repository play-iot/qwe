package io.zero88.qwe.sql.validation;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.zero88.qwe.sql.CompositeMetadata;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.pojos.CompositePojo;
import io.zero88.qwe.sql.pojos.JsonPojo;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.TripleFunction;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.exceptions.QWEExceptionConverter;
import io.zero88.qwe.utils.JsonUtils;

import lombok.NonNull;

/**
 * Represents for composite entity validation
 *
 * @param <P> Type of {@code JsonRecord}
 * @param <C> Type of {@code Composite Pojo} that extends from Vertx pojo type
 * @see CompositePojo
 * @see EntityValidation
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public interface CompositeValidation<P extends JsonRecord, C extends CompositePojo<P, C>> extends EntityValidation<P> {

    /**
     * @see CompositeMetadata
     */
    @Override
    CompositeMetadata context();

    @Override
    @NonNull
    default C onCreating(RequestData reqData) throws IllegalArgumentException {
        final C request = (C) context().parseFromRequest(reqData.body());
        for (EntityMetadata metadata : ((List<EntityMetadata>) context().subItems())) {
            String key = metadata.singularKeyName();
            final JsonRecord sub = request.getOther(key);
            if (Objects.isNull(sub)) {
                continue;
            }
            request.put(key, Functions.getOrThrow(
                () -> metadata.onCreating(RequestData.builder().body(reqData.body().getJsonObject(key)).build()),
                QWEExceptionConverter::from));
        }
        return request;
    }

    @Override
    default <PP extends P> @NonNull PP onUpdating(@NonNull P dbData, RequestData reqData)
        throws IllegalArgumentException {
        C request = (C) context().parseFromRequest(reqData.body());
        validateSubEntities((C) dbData, request,
                            (metadata, requestData, pojo) -> metadata.onUpdating(pojo, requestData));
        return (PP) request;
    }

    @Override
    default <PP extends P> @NonNull PP onPatching(@NonNull P dbData, RequestData reqData)
        throws IllegalArgumentException {
        C request = (C) context().parseFromRequest(JsonPojo.merge(((C) dbData).toJsonWithoutExt(), reqData.body()));
        validateSubEntities((C) dbData, request,
                            (metadata, requestData, pojo) -> metadata.onPatching(pojo, requestData));
        return (PP) request;
    }

    @Override
    default <PP extends P> PP onDeleting(P dbData, @NonNull RequestData reqData) throws IllegalArgumentException {
        return (PP) dbData;
    }

    /**
     * Construct error message.
     *
     * @param data       the data
     * @param references the references
     * @return error message
     * @since 1.0.0
     */
    default String msg(@NonNull JsonObject data, @NonNull Collection<EntityMetadata> references) {
        return references.stream()
                         .filter(Objects::nonNull)
                         .filter(m -> !context().requestKeyName().equals(m.requestKeyName()))
                         .map(ref -> new SimpleEntry<>(ref.requestKeyName(), data.getValue(ref.requestKeyName())))
                         .filter(entry -> Objects.nonNull(entry.getValue()))
                         .map(JsonUtils.kvMsg())
                         .collect(Collectors.joining(" and "));
    }

    /**
     * Validate and re-update children {@code entities} by current composite entity.
     *
     * @param dbData    the db data
     * @param request   the request
     * @param validator the validator
     * @throws QWEException if catching any invalid value
     * @see CompositeMetadata#subItems()
     * @since 1.0.0
     */
    default void validateSubEntities(@NonNull C dbData, @NonNull C request,
                                     TripleFunction<EntityMetadata, RequestData, JsonRecord, JsonRecord> validator) {
        for (EntityMetadata metadata : ((List<EntityMetadata>) context().subItems())) {
            final String key = metadata.singularKeyName();
            final JsonRecord sub = request.getOther(key);
            if (Objects.isNull(sub)) {
                continue;
            }
            try {
                request.put(key, validator.apply(metadata, RequestData.builder().body(sub.toJson()).build(),
                                                 dbData.safeGetOther(key, metadata.modelClass())));
            } catch (Exception e) {
                throw QWEExceptionConverter.from(e);
            }
        }
    }

}

package io.zero88.qwe.sql.service.transformer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.Status;
import io.zero88.qwe.sql.pojos.JsonPojo;

import lombok.NonNull;

/**
 * A {@code transformer} that transform {@code entity resource} before response to client
 *
 * @since 1.0.0
 */
public interface EntityTransformer {

    /**
     * Represents set of audit fields. Use {@link RequestFilter#AUDIT} to expose these fields
     */
    Set<String> AUDIT_FIELDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("time_audit", "sync_audit")));

    /**
     * Construct a success {@code CUD response} that includes full resource.
     *
     * @param action Event action
     * @param result Result data
     * @return response json object
     * @since 1.0.0
     */
    static JsonObject fullResponse(@NonNull EventAction action, @NonNull JsonObject result) {
        return new JsonObject().put("resource", result).put("action", action).put("status", Status.SUCCESS);
    }

    /**
     * Key response json object.
     *
     * @param keyName  the key name
     * @param keyValue the key value
     * @return the json object
     * @since 1.0.0
     */
    static JsonObject keyResponse(@NonNull String keyName, @NonNull Object keyValue) {
        return new JsonObject().put(keyName, JsonData.checkAndConvert(keyValue));
    }

    /**
     * Resource metadata entity metadata.
     *
     * @return resource metadata
     * @since 1.0.0
     */
    @NonNull EntityMetadata resourceMetadata();

    /**
     * Enable {@code CUD} response includes full resource instead of simple resource with only response status and
     * {@code primary key} of resource.
     *
     * @return {@code true} if enable full resource in response
     * @since 1.0.0
     */
    default boolean enableFullResourceInCUDResponse() {
        return true;
    }

    /**
     * Get ignore fields by request data. By default, it excludes {@link #AUDIT_FIELDS}.
     * <p>
     * If {@code request data} has {@link RequestFilter#AUDIT}, it will expose these {@code audit fields}
     *
     * @param requestData Request data
     * @return set of ignore fields when do customizing response data
     * @see #afterCreate(Object, JsonRecord, RequestData)
     * @see #afterDelete(JsonRecord, RequestData)
     * @see #afterUpdate(Object, JsonRecord, RequestData)
     * @see #afterPatch(Object, JsonRecord, RequestData)
     * @see #afterGet(JsonRecord, RequestData)
     * @see #afterEach(JsonRecord, RequestData)
     * @since 1.0.0
     */
    default Set<String> ignoreFields(@NonNull RequestData requestData) {
        return requestData.filter().hasAudit() ? Collections.emptySet() : AUDIT_FIELDS;
    }

    /**
     * Do any transformation after query each resource in database entities
     *
     * @param pojo        database entity
     * @param requestData request data
     * @return transformer item
     * @apiNote By default, result omits {@code null} fields
     * @since 1.0.0
     */
    default @NonNull Future<JsonObject> afterEach(@NonNull JsonRecord pojo, @NonNull RequestData requestData) {
        return Future.succeededFuture(JsonPojo.from(pojo).toJson(ignoreFields(requestData)));
    }

    /**
     * Wrap list data to json object
     *
     * @param results given results
     * @return json object of list data
     * @since 1.0.0
     */
    @NonNull
    default Future<JsonObject> afterList(@NonNull JsonArray results) {
        return Future.succeededFuture(new JsonObject().put(resourceMetadata().pluralKeyName(), results));
    }

    /**
     * Do any transform for single resource
     *
     * @param pojo        item
     * @param requestData request data
     * @return transformer item
     * @apiNote By default, result omits {@code null} fields
     * @since 1.0.0
     */
    @NonNull
    default Future<JsonObject> afterGet(@NonNull JsonRecord pojo, @NonNull RequestData requestData) {
        return Future.succeededFuture(JsonPojo.from(pojo).toJson(ignoreFields(requestData)));
    }

    /**
     * Do any transform resource after {@code CREATE} action successfully if {@link #enableFullResourceInCUDResponse()}
     * is {@code true}
     *
     * @param key     pojo key
     * @param pojo    item
     * @param reqData request data
     * @return transformer item
     * @apiNote By default, result doesn't omits {@code null} fields
     * @since 1.0.0
     */
    @NonNull
    default Future<JsonObject> afterCreate(@NonNull Object key, @NonNull JsonRecord pojo,
                                           @NonNull RequestData reqData) {
        return Future.succeededFuture(doTransform(EventAction.CREATE, key, pojo, reqData, (p, r) -> JsonPojo.from(pojo)
                                                                                                            .toJson(
                                                                                                                JsonData.MAPPER,
                                                                                                                ignoreFields(
                                                                                                                    reqData))));
    }

    /**
     * Do any transform resource after {@code UPDATE} action successfully if {@link #enableFullResourceInCUDResponse()}
     * is {@code true}
     *
     * @param key     pojo key
     * @param pojo    item
     * @param reqData request data
     * @return transformer item
     * @apiNote By default, result omits {@code null} fields and {@link #AUDIT_FIELDS}
     * @since 1.0.0
     */
    default @NonNull Future<JsonObject> afterUpdate(@NonNull Object key, @NonNull JsonRecord pojo,
                                                    @NonNull RequestData reqData) {
        return Future.succeededFuture(doTransform(EventAction.UPDATE, key, pojo, reqData, (p, r) -> JsonPojo.from(pojo)
                                                                                                            .toJson(
                                                                                                                JsonPojo.MAPPER,
                                                                                                                ignoreFields(
                                                                                                                    reqData))));
    }

    /**
     * Do any transform resource after {@code PATCH} action successfully if {@link #enableFullResourceInCUDResponse()}
     * is {@code true}
     *
     * @param key     pojo key
     * @param pojo    item
     * @param reqData request data
     * @return transformer item
     * @apiNote By default, result omits {@code null} fields and {@link #AUDIT_FIELDS}
     * @since 1.0.0
     */
    @NonNull
    default Future<JsonObject> afterPatch(@NonNull Object key, @NonNull JsonRecord pojo, @NonNull RequestData reqData) {
        return Future.succeededFuture(doTransform(EventAction.PATCH, key, pojo, reqData, (p, r) -> JsonPojo.from(pojo)
                                                                                                           .toJson(
                                                                                                               JsonPojo.MAPPER,
                                                                                                               ignoreFields(
                                                                                                                   reqData))));
    }

    /**
     * Do any transform resource after {@code DELETE} action successfully if {@link #enableFullResourceInCUDResponse()}
     * is {@code true}
     *
     * @param pojo    item
     * @param reqData request data
     * @return transformer item
     * @apiNote By default, result doesn't omits {@code null} fields
     * @since 1.0.0
     */
    @NonNull
    default Future<JsonObject> afterDelete(@NonNull JsonRecord pojo, @NonNull RequestData reqData) {
        return Future.succeededFuture(doTransform(EventAction.REMOVE, pojo, reqData, (p, r) -> JsonPojo.from(p)
                                                                                                       .toJson(
                                                                                                           JsonData.MAPPER,
                                                                                                           ignoreFields(
                                                                                                               reqData)),
                                                  JsonObject::new));
    }

    /**
     * Do transform json object.
     *
     * @param action    the action
     * @param key       the key
     * @param pojo      the pojo
     * @param reqData   the req data
     * @param converter the converter
     * @return the json object
     * @since 1.0.0
     */
    default JsonObject doTransform(EventAction action, Object key, JsonRecord pojo, RequestData reqData,
                                   BiFunction<JsonRecord, RequestData, JsonObject> converter) {
        return doTransform(action, pojo, reqData, converter,
                           () -> keyResponse(resourceMetadata().requestKeyName(), key));
    }

    /**
     * Do transform json object.
     *
     * @param action    the action
     * @param pojo      the pojo
     * @param reqData   the req data
     * @param converter the converter
     * @param ifNotFull the if not full
     * @return the json object
     * @since 1.0.0
     */
    default JsonObject doTransform(EventAction action, JsonRecord pojo, RequestData reqData,
                                   BiFunction<JsonRecord, RequestData, JsonObject> converter,
                                   Supplier<JsonObject> ifNotFull) {
        if (enableFullResourceInCUDResponse()) {
            return fullResponse(action, converter.apply(pojo, reqData));
        } else {
            return ifNotFull.get();
        }
    }

}

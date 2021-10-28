package io.zero88.qwe.sql.service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EBContract;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.http.httpevent.ActionMethodMapping;
import io.zero88.qwe.sql.EntityMetadata;

import lombok.NonNull;

/**
 * Represents for {@code entity service} in batch mode.
 *
 * @param <P> Type of {@code JsonRecord}
 * @param <M> Type of {@code EntityMetadata}
 * @see EntityService
 * @since 1.0.0
 */
public interface BatchEntityService<P extends JsonRecord, M extends EntityMetadata> extends EntityService<P, M> {

    @NonNull
    default Collection<EventAction> getAvailableEvents() {
        return Stream.of(EntityService.super.getAvailableEvents(), ActionMethodMapping.BATCH_DML_MAP.get().keySet())
                     .flatMap(Collection::stream)
                     .collect(Collectors.toSet());
    }

    /**
     * Defines listener for updating existing resources in batch
     *
     * @param requestData Request data
     * @return json object that includes status message
     * @see EventAction#BATCH_CREATE
     * @since 1.0.0
     */
    @EBContract(action = "BATCH_CREATE")
    Future<JsonObject> batchCreate(@NonNull RequestData requestData);

    /**
     * Defines listener for updating existing resources in batch
     *
     * @param requestData Request data
     * @return json object that includes status message
     * @see EventAction#BATCH_UPDATE
     * @since 1.0.0
     */
    @EBContract(action = "BATCH_UPDATE")
    Future<JsonObject> batchUpdate(@NonNull RequestData requestData);

    /**
     * Defines listener for patching existing resources in batch
     *
     * @param requestData Request data
     * @return json object that includes status message
     * @see EventAction#BATCH_PATCH
     * @since 1.0.0
     */
    @EBContract(action = "BATCH_PATCH")
    Future<JsonObject> batchPatch(@NonNull RequestData requestData);

    /**
     * Defines listener for deleting existing resources in batch
     *
     * @param requestData Request data
     * @return json object that includes status message
     * @see EventAction#BATCH_DELETE
     * @since 1.0.0
     */
    @EBContract(action = "BATCH_DELETE")
    Future<JsonObject> batchDelete(@NonNull RequestData requestData);

}

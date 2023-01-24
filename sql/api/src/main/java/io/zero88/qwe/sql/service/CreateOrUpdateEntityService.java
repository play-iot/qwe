package io.zero88.qwe.sql.service;

import java.util.Collection;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EBContract;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.exceptions.DataNotFoundException;
import io.zero88.qwe.http.httpevent.ActionMethodMapping;
import io.zero88.qwe.sql.EntityMetadata;

import lombok.NonNull;

/**
 * Represents for an {@code entity service} that supports {@code create_or_update} event
 *
 * @param <P> Type of {@code JsonRecord}
 * @param <M> Type of {@code EntityMetadata}
 * @since 1.0.0
 */
public interface CreateOrUpdateEntityService<P extends JsonRecord, M extends EntityMetadata>
    extends EntityService<P, M> {

    @Override
    default @NonNull Collection<EventAction> getAvailableEvents() {
        return ActionMethodMapping.CRD_MAP.get().keySet();
    }

    /**
     * Create or update entity.
     *
     * @param requestData the request data
     * @return json result
     * @since 1.0.0
     */
    @EBContract(action = "CREATE_OR_UPDATE")
    default Future<JsonObject> createOrUpdate(@NonNull RequestData requestData) {
        return patch(requestData).recover(t -> {
            if (t instanceof DataNotFoundException) {
                return create(requestData);
            }
            return Future.failedFuture(t);
        });
    }

}

package io.zero88.qwe.sql.handler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.UpdateConditionStep;

import io.github.zero88.utils.Strings;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.Status;
import io.zero88.qwe.sql.pojos.HasSyncAudit;
import io.zero88.qwe.sql.pojos.HasTimeAudit;
import io.zero88.qwe.sql.pojos.JsonPojo;
import io.zero88.qwe.sql.type.SyncAudit;

import lombok.NonNull;

/**
 * Helper to add or update sync audit status
 *
 * @see HasSyncAudit
 * @see SyncAudit
 * @since 1.0.0
 */
public interface EntitySyncHandler<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                      E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends HasEntityHandler<S, B, PQ, RS, RC, E> {

    /**
     * Force not synced audit on creating if current entity is child of {@link HasSyncAudit}
     *
     * @param <P>  Pojo type
     * @param pojo Given entity
     * @return modified pojo for fluent API
     * @see SyncAudit#notYetSynced(String)
     * @since 1.0.0
     */
    static <P extends JsonRecord> P markNotSyncedOnCreating(@NonNull P pojo) {
        if (pojo instanceof HasSyncAudit) {
            ((HasSyncAudit) pojo).setSyncAudit(SyncAudit.notYetSynced("Not yet synced new resource"));
        }
        return pojo;
    }

    /**
     * Force not synced audit on creating if current entity is child of {@link HasSyncAudit}
     *
     * @param <P>  Type of {@code JsonRecord}
     * @param pojo Given entity
     * @return modified pojo for fluent API
     * @see SyncAudit#notYetSynced(SyncAudit, String)
     * @since 1.0.0
     */
    static <P extends JsonRecord> P markNotSyncedOnModified(@NonNull P pojo) {
        if (pojo instanceof HasSyncAudit) {
            final HasSyncAudit syncPojo = (HasSyncAudit) pojo;
            final SyncAudit prevSync = Optional.ofNullable(syncPojo.getSyncAudit()).orElse(SyncAudit.unknown());
            final Integer revision = pojo instanceof HasTimeAudit
                                     ? ((HasTimeAudit) pojo).getTimeAudit().getRevision()
                                     : null;
            final String message = Strings.format("Not yet synced modified resource{0}",
                                                  Objects.isNull(revision) ? "" : " with record revision " + revision);
            syncPojo.setSyncAudit(SyncAudit.notYetSynced(prevSync, message));
        }
        return pojo;
    }

    /**
     * Updates Sync success status.
     *
     * @param <P>      Type of {@code JsonRecord}
     * @param metadata the metadata
     * @param pojo     the pojo
     * @param response the response
     * @param by       the by
     * @return json result in maybe
     * @since 1.0.0
     */
    default <P extends JsonRecord> Future<@Nullable JsonObject> syncSuccess(@NonNull EntityMetadata metadata,
                                                                            @NonNull P pojo, JsonObject response,
                                                                            String by) {
        if (!(pojo instanceof HasSyncAudit)) {
            return Future.succeededFuture(null);
        }
        final SyncAudit audit = Optional.ofNullable(((HasSyncAudit) pojo).getSyncAudit()).orElse(SyncAudit.unknown());
        final SyncAudit syncAudit = SyncAudit.success(audit, response, by);
        return updateSyncedStatus(metadata, pojo, syncAudit);
    }

    /**
     * Updates Sync failed status.
     *
     * @param <P>      Type of {@code JsonRecord}
     * @param metadata the metadata
     * @param pojo     the pojo
     * @param t        the t
     * @param by       the by
     * @return json result in maybe
     * @since 1.0.0
     */
    default <P extends JsonRecord> Future<@Nullable JsonObject> syncFailed(@NonNull EntityMetadata metadata,
                                                                           @NonNull P pojo, @NonNull Throwable t,
                                                                           String by) {
        if (!(pojo instanceof HasSyncAudit)) {
            return Future.succeededFuture(null);
        }
        final SyncAudit audit = Optional.ofNullable(((HasSyncAudit) pojo).getSyncAudit()).orElse(SyncAudit.unknown());
        final SyncAudit syncAudit = SyncAudit.error(audit, ErrorMessage.parse(t).toJson(), by);
        return updateSyncedStatus(metadata, pojo, syncAudit);
    }

    /**
     * Updates synced status.
     *
     * @param <P>       Type of {@code JsonRecord}
     * @param metadata  the metadata
     * @param pojo      the pojo
     * @param syncAudit the sync audit
     * @return json result in maybe
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    default <P extends JsonRecord> Future<@Nullable JsonObject> updateSyncedStatus(@NonNull EntityMetadata metadata,
                                                                                   @NonNull P pojo,
                                                                                   @NonNull SyncAudit syncAudit) {
        final Object key = pojo.toJson().getValue(metadata.jsonKeyName());
        final Condition condition = metadata.table().getField(metadata.jsonKeyName()).eq(key);
        final HasSyncAudit updated = (HasSyncAudit) metadata.parseFromEntity(new JsonObject());
        final Map<Field, Object> sync = JsonPojo.from(updated.setSyncAudit(syncAudit))
                                                .toJson()
                                                .stream()
                                                .collect(Collectors.toMap(k -> metadata.table().getField(k.getKey()),
                                                                          Entry::getValue));

        final UpdateConditionStep query = entityHandler().dsl().update(metadata.table()).set(sync).where(condition);
        return entityHandler().jooqx()
                              .execute(dsl -> query, DSLAdapter.fetchCount(query.asTable()))
                              .map(r -> new JsonObject().put("action", EventAction.UPDATE)
                                                        .put("status", r > 0 ? Status.SUCCESS : Status.FAILED)
                                                        .put("record", r)
                                                        .put("key", key)
                                                        .put("resource", metadata.table().getName())
                                                        .put("sync_audit", syncAudit.toJson()));
    }

}

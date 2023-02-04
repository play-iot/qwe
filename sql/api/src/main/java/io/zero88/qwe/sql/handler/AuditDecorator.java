package cloud.playio.qwe.sql.handler;

import java.util.Optional;

import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.Status;
import cloud.playio.qwe.sql.pojos.HasTimeAudit;
import cloud.playio.qwe.sql.type.TimeAudit;
import io.github.zero88.utils.Strings;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.micro.GatewayHeaders;
import cloud.playio.qwe.sql.handler.EntitySyncHandler;

import lombok.NonNull;

/**
 * Decorator {@code entity} with {@code audit} properties before persist in database
 *
 * @see HasTimeAudit
 * @see EntitySyncHandler
 * @since 1.0.0
 */
//TODO MOVE to DAO internal
public interface AuditDecorator {

    /**
     * Add creation audit for resource.
     *
     * @param <P>      Type of {@code JsonRecord}
     * @param reqData  the request data
     * @param metadata the metadata
     * @param pojo     the pojo
     * @return modified pojo for fluent API
     * @since 1.0.0
     */
    static <P extends JsonRecord> P addCreationAudit(@NonNull RequestData reqData, @NonNull EntityMetadata metadata,
                                                     @NonNull P pojo) {
        final String createdBy = reqData.headers().getString(GatewayHeaders.X_REQUEST_USER, Status.UNDEFINED.type());
        return addCreationAudit(metadata.enableTimeAudit(), pojo, createdBy);
    }

    /**
     * Add creation audit for resource.
     *
     * @param <P>          Type of {@code JsonRecord}
     * @param auditEnabled the audit enabled
     * @param pojo         the pojo
     * @param createdBy    the created by
     * @return modified pojo for fluent API
     * @since 1.0.0
     */
    static <P extends JsonRecord> P addCreationAudit(boolean auditEnabled, @NonNull P pojo, String createdBy) {
        return addCreationTimeAudit(auditEnabled, EntitySyncHandler.markNotSyncedOnCreating(pojo), createdBy);
    }

    /**
     * Add modified audit for resource.
     *
     * @param <P>      Type of {@code JsonRecord}
     * @param reqData  the req data
     * @param metadata the metadata
     * @param pojo     the pojo
     * @return modified pojo for fluent API
     * @since 1.0.0
     */
    static <P extends JsonRecord> P addModifiedAudit(@NonNull RequestData reqData, @NonNull EntityMetadata metadata,
                                                     @NonNull P pojo) {
        return addModifiedAudit(reqData, metadata, null, pojo);
    }

    /**
     * Add modified audit for resource.
     *
     * @param <P>      Type of {@code JsonRecord}
     * @param reqData  the req data
     * @param metadata the metadata
     * @param dbData   the db data
     * @param pojo     the pojo
     * @return modified pojo for fluent API
     * @since 1.0.0
     */
    static <P extends JsonRecord> P addModifiedAudit(@NonNull RequestData reqData, @NonNull EntityMetadata metadata,
                                                     P dbData, @NonNull P pojo) {
        final String modifiedBy = reqData.headers().getString(GatewayHeaders.X_REQUEST_USER, null);
        return addModifiedAudit(metadata.enableTimeAudit(), dbData, pojo, modifiedBy);
    }

    /**
     * Add modified audit for resource.
     *
     * @param <P>          Type of {@code JsonRecord}
     * @param auditEnabled the audit enabled
     * @param dbData       the db data
     * @param pojo         the pojo
     * @param modifiedBy   the modified by
     * @return modified pojo for fluent API
     * @since 1.0.0
     */
    static <P extends JsonRecord> P addModifiedAudit(boolean auditEnabled, P dbData, @NonNull P pojo,
                                                     String modifiedBy) {
        return EntitySyncHandler.markNotSyncedOnModified(addModifiedTimeAudit(auditEnabled, dbData, pojo, modifiedBy));
    }

    /**
     * Add creation time audit if current entity is child of {@link HasTimeAudit}
     *
     * @param <P>          Type of {@code JsonRecord}
     * @param auditEnabled Whether enable audit or not
     * @param pojo         Given entity
     * @param createdBy    Created by
     * @return modified pojo for fluent API
     * @since 1.0.0
     */
    static <P extends JsonRecord> P addCreationTimeAudit(boolean auditEnabled, @NonNull P pojo, String createdBy) {
        if (auditEnabled && pojo instanceof HasTimeAudit) {
            ((HasTimeAudit) pojo).setTimeAudit(TimeAudit.created(Strings.fallback(createdBy, Status.UNDEFINED.type())));
        }
        return pojo;
    }

    /**
     * Add modified time audit if current entity is child of {@link HasTimeAudit}
     *
     * @param <P>          Type of {@code JsonRecord}
     * @param auditEnabled Whether enable audit or not
     * @param dbData       Existed entity in database
     * @param pojo         Given entity
     * @param modifiedBy   Modified by
     * @return modified pojo for fluent API
     * @since 1.0.0
     */
    static <P extends JsonRecord> P addModifiedTimeAudit(boolean auditEnabled, P dbData, @NonNull P pojo,
                                                         String modifiedBy) {
        if (auditEnabled && pojo instanceof HasTimeAudit) {
            TimeAudit prev = Optional.ofNullable(dbData).map(p -> ((HasTimeAudit) p).getTimeAudit()).orElse(null);
            ((HasTimeAudit) pojo).setTimeAudit(
                TimeAudit.modified(prev, Strings.fallback(modifiedBy, Status.UNDEFINED.type())));
        }
        return pojo;
    }

}

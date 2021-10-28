package io.zero88.qwe.sql.workflow.task;

import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.eventbus.EventBusClient;
import io.zero88.qwe.sql.pojos.DMLPojo;
import io.zero88.qwe.sql.query.EntityQueryExecutor;
import io.zero88.qwe.workflow.Task;

import lombok.NonNull;

/**
 * Represents Entity Task
 *
 * @param <DC> Type of {@code EntityTaskContext}
 * @param <P>  Type of {@code JsonRecord}
 * @param <R>  Type of {@code Result}
 * @see Task
 * @see EntityDefinitionContext
 * @see EntityRuntimeContext
 * @since 1.0.0
 */
public interface EntityTask<DC extends EntityDefinitionContext, P extends JsonRecord, R>
    extends Task<DC, EntityRuntimeContext<P>, R> {

    interface EntityNormalTask<DC extends EntityDefinitionContext, P extends JsonRecord, R>
        extends EntityTask<DC, P, R> {

    }


    interface EntityPurgeTask<DC extends PurgeDefinitionContext, P extends JsonRecord, R>
        extends EntityTask<DC, P, R>, ProxyEntityTask<DC, P, R, EventBusClient> {

        static <P extends JsonRecord> EntityPurgeTask<PurgeDefinitionContext, P, DMLPojo> create(
            @NonNull EntityQueryExecutor queryExecutor, boolean supportForceDeletion) {
            return new DefaultEntityPurgeTask<>(PurgeDefinitionContext.create(queryExecutor), supportForceDeletion);
        }

        @Override
        default EventBusClient transporter() {
            return definitionContext().entityHandler().transporter();
        }

    }

}

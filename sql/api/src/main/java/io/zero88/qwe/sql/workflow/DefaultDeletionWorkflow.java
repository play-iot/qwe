package io.zero88.qwe.sql.workflow;

import io.vertx.core.Future;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.pojos.DMLPojo;
import io.zero88.qwe.sql.validation.OperationValidator;
import io.zero88.qwe.sql.workflow.step.DeletionStep;
import io.zero88.qwe.sql.workflow.task.EntityTask.EntityPurgeTask;
import io.zero88.qwe.workflow.TaskExecuter;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class DefaultDeletionWorkflow extends DefaultDMLWorkflow<DeletionStep> {

    private final boolean supportForceDeletion;

    @Override
    protected @NonNull OperationValidator afterValidation() {
        return super.afterValidation().andThen(OperationValidator.create(this::purgeTask));
    }

    private Future<JsonRecord> purgeTask(@NonNull RequestData reqData, @NonNull JsonRecord pojo) {
        return TaskExecuter.execute(EntityPurgeTask.create(sqlStep().queryExecutor(), supportForceDeletion),
                                    initSuccessData(reqData, pojo)).map(DMLPojo::request);
    }

}

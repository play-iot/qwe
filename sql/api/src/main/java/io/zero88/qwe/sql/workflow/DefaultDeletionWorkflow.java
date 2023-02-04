package cloud.playio.qwe.sql.workflow;

import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.pojos.DMLPojo;
import cloud.playio.qwe.sql.validation.OperationValidator;
import cloud.playio.qwe.sql.workflow.step.DeletionStep;
import cloud.playio.qwe.sql.workflow.task.EntityTask.EntityPurgeTask;
import cloud.playio.qwe.workflow.TaskExecuter;

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

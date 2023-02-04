package cloud.playio.qwe.sql.workflow;

import java.util.function.Function;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.transaction.JDBCRXTransactionExecutor;
import cloud.playio.qwe.sql.validation.OperationValidator;
import cloud.playio.qwe.sql.workflow.step.DMLStep;
import cloud.playio.qwe.sql.workflow.task.EntityTaskManager;

import lombok.Builder;
import lombok.NonNull;

@Builder
public final class DefaultDMLTransactionWorkflow implements DMLTransactionWorkflow {

    @NonNull
    private final DMLWorkflow workflow;

    @Override
    public @NonNull EventAction action() {
        return workflow.action();
    }

    @Override
    public @NonNull EntityMetadata metadata() {
        return workflow.metadata();
    }

    @Override
    public @NonNull Function<RequestData, RequestData> normalize() {
        return workflow.normalize();
    }

    @Override
    public @NonNull OperationValidator validator() {
        return workflow.validator();
    }

    @Override
    public @NonNull EntityTaskManager taskManager() {
        return workflow.taskManager();
    }

    @Override
    public @NonNull Future<JsonObject> run(@NonNull RequestData reqData) {
        return JDBCRXTransactionExecutor.create(sqlStep().queryExecutor().entityHandler().dsl())
                                        .transactionResult(c -> ((AbstractSQLWorkflow) workflow).run(reqData, c));
    }

    @Override
    public @NonNull DMLStep sqlStep() {
        return workflow.sqlStep();
    }

}

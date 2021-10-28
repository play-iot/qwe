package io.zero88.qwe.sql.workflow;

import java.util.function.Function;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.transaction.JDBCRXTransactionExecutor;
import io.zero88.qwe.sql.validation.OperationValidator;
import io.zero88.qwe.sql.workflow.step.DMLStep;
import io.zero88.qwe.sql.workflow.task.EntityTaskManager;

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

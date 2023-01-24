package io.zero88.qwe.sql.workflow;

import java.util.function.Function;

import org.jooq.Configuration;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.validation.OperationValidator;
import io.zero88.qwe.sql.workflow.task.EntityRuntimeContext;
import io.zero88.qwe.sql.workflow.task.EntityTaskManager;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
abstract class AbstractSQLWorkflow implements SQLWorkflow {

    @NonNull
    private final EventAction action;
    @NonNull
    private final EntityMetadata metadata;
    @NonNull
    private final Function<RequestData, RequestData> normalize;
    @NonNull
    private final OperationValidator validator;
    @NonNull
    private final EntityTaskManager taskManager;

    @Override
    public final @NonNull Future<JsonObject> run(@NonNull RequestData requestData) {
        return run(requestData, null);
    }

    @NonNull
    protected abstract Future<JsonObject> run(@NonNull RequestData requestData, Configuration runtimeConfig);

    @NonNull
    protected OperationValidator afterValidation() {
        return OperationValidator.create((req, pojo) -> taskManager().preBlockingExecuter()
                                                                     .execute(initSuccessData(req, pojo))
                                                                     .switchIfEmpty(Future.succeededFuture(pojo)));
    }

    @NonNull
    protected EntityRuntimeContext<JsonRecord> initSuccessData(@NonNull RequestData reqData, @NonNull JsonRecord pojo) {
        return taskData(reqData, pojo, null);
    }

    @NonNull
    protected EntityRuntimeContext<JsonRecord> initErrorData(@NonNull RequestData reqData, @NonNull Throwable err) {
        return taskData(reqData, null, err);
    }

    @NonNull
    protected EntityRuntimeContext<JsonRecord> taskData(@NonNull RequestData reqData, JsonRecord pojo, Throwable t) {
        return EntityRuntimeContext.builder()
                                   .originReqData(reqData)
                                   .originReqAction(action())
                                   .metadata(metadata())
                                   .data(pojo)
                                   .throwable(t)
                                   .build();
    }

}

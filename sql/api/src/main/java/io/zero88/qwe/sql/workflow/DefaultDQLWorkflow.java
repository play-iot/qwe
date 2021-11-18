package io.zero88.qwe.sql.workflow;

import java.util.function.BiFunction;

import org.jooq.Configuration;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.workflow.step.DQLStep;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class DefaultDQLWorkflow<T extends JsonRecord> extends AbstractSQLWorkflow implements DQLWorkflow<T> {

    @NonNull
    private final DQLStep<T> sqlStep;
    @NonNull
    private final BiFunction<RequestData, T, Future<JsonObject>> transformer;

    @Override
    @SuppressWarnings("unchecked")
    protected @NonNull Future<JsonObject> run(@NonNull RequestData requestData, Configuration runtimeConfig) {
        final RequestData reqData = normalize().apply(requestData);
        return sqlStep().query(reqData, validator().andThen(afterValidation()))
                        .flatMap(pojo -> taskManager().postBlockingExecuter()
                                                      .execute(initSuccessData(reqData, pojo))
                                                      .recover(r -> Future.succeededFuture(pojo)))
                        .onSuccess(pojo -> taskManager().postAsyncExecuter().execute(initSuccessData(reqData, pojo)))
                        .onFailure(err -> taskManager().postAsyncExecuter().execute(initErrorData(reqData, err)))
                        .flatMap(pojo -> transformer().apply(reqData, (T) pojo));
    }

}

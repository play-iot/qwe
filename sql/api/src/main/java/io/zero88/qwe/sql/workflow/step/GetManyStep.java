package io.zero88.qwe.sql.workflow.step;

import java.util.function.BiFunction;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.validation.OperationValidator;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Accessors(fluent = true)
@SuppressWarnings("unchecked")
public final class GetManyStep extends AbstractSQLStep implements DQLBatchStep {

    @Getter
    @NonNull
    private BiFunction<JsonRecord, RequestData, Future<JsonObject>> onEach;

    @Override
    public Future<JsonArray> query(@NonNull RequestData reqData, @NonNull OperationValidator validator) {
        return queryExecutor().findMany(reqData)
                              .flatMap(pojo -> onEach().apply((JsonRecord) pojo, reqData))
                              .collect(JsonArray::new, (array, obj) -> ((JsonArray) array).add(obj));
    }

}

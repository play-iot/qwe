package io.zero88.qwe.sql.workflow.step;

import java.util.function.BiFunction;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.validation.OperationValidator;

import lombok.NonNull;

public interface DQLBatchStep extends SQLBatchStep {

    @NonNull BiFunction<JsonRecord, RequestData, Future<JsonObject>> onEach();

    /**
     * Do {@code SQL Query} based on given {@code request data} and {@code validator}.
     *
     * @param reqData   the req data
     * @param validator the validator
     * @return result in Single
     * @see RequestData
     * @see OperationValidator
     * @since 1.0.0
     */
    Future<JsonArray> query(@NonNull RequestData reqData, @NonNull OperationValidator validator);

}

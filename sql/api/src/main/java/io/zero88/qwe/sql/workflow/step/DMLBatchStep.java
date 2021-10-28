package io.zero88.qwe.sql.workflow.step;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.pojos.DMLPojo;
import io.zero88.qwe.sql.validation.OperationValidator;

import lombok.NonNull;

/**
 * Represents a {@code DML} step on the entity resources in batch
 *
 * @since 1.0.0
 */
public interface DMLBatchStep extends SQLBatchStep {

    /**
     * Execute {@code SQL manipulate command} based on given {@code request data} and {@code validator}.
     *
     * @param reqData   the req data
     * @param validator the validator
     * @return json result in Single
     * @see RequestData
     * @see OperationValidator
     * @see DMLPojo
     * @since 1.0.0
     */
    Future<JsonObject> execute(@NonNull RequestData reqData, @NonNull OperationValidator validator);

}

package cloud.playio.qwe.sql.workflow.step;

import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.validation.OperationValidator;

import lombok.NonNull;

/**
 * Represents a {@code DQL} step
 *
 * @param <T> Type of {@code JsonRecord}
 * @since 1.0.0
 */
public interface DQLStep<T extends JsonRecord> extends SQLStep {

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
    Future<T> query(@NonNull RequestData reqData, @NonNull OperationValidator validator);

}

package cloud.playio.qwe.sql.validation;

import java.util.function.BiFunction;

import org.jooq.Record;

import io.vertx.core.Future;
import cloud.playio.qwe.dto.msg.RequestData;

import lombok.NonNull;

/**
 * Represents for {@code DML} or {@code DQL} validator before do execute {@code SQL operation} in database
 * <p>
 * It can be used to validate request data or check entity permission
 *
 * @since 1.0.0
 */
public interface OperationValidator<R extends Record> {

    /**
     * Create operation validator.
     *
     * @param validation the validation function
     * @return the operation validator
     * @since 1.0.0
     */
    @NonNull
    static <R extends Record> OperationValidator<R> create(@NonNull BiFunction<RequestData, R, Future<R>> validation) {
        return new DefaultOperationValidator<>(validation);
    }

    /**
     * Validate entity from request data
     *
     * @param reqData  request data
     * @param dbEntity previous entity. It can be {@code null} in case of {@code Create}
     * @return entity after validate
     * @since 1.0.0
     */
    @NonNull Future<R> validate(@NonNull RequestData reqData, R dbEntity);

    /**
     * Defines action after validating.
     * <p>
     * It can be used to inject an extra validator such as the permission validation on each record step, etc
     *
     * @param andThen extra validator
     * @return a reference to this, so the API can be used fluently
     * @since 1.0.0
     */
    @NonNull OperationValidator<R> andThen(OperationValidator<R> andThen);

}

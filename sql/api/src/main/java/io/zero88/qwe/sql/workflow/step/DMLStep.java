package cloud.playio.qwe.sql.workflow.step;

import org.jooq.Configuration;

import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.pojos.DMLPojo;
import cloud.playio.qwe.sql.validation.OperationValidator;

import lombok.NonNull;

/**
 * Represents a {@code DML} step for single resource
 *
 * @since 1.0.0
 */
public interface DMLStep extends SQLStep {

    /**
     * Execute {@code SQL manipulate command} based on given {@code request data} and {@code validator}.
     *
     * @param requestData   the sql runtime context
     * @param validator     the validator
     * @param configuration the configuration
     * @return DML pojo in Single
     * @see OperationValidator
     * @see DMLPojo
     * @since 1.0.0
     */
    Future<DMLPojo> execute(@NonNull RequestData requestData, @NonNull OperationValidator validator,
                            Configuration configuration);

    /**
     * Represents a {@code create} or {@code update} step
     *
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    interface CreateOrUpdateStep extends DMLStep {

        /**
         * Lookup created or modified entity by primary key
         *
         * @param dmlPojo Request pojo with primary key
         * @return wrapper pojo
         * @see DMLPojo
         * @since 1.0.0
         */
        default Future<DMLPojo> lookup(@NonNull DMLPojo dmlPojo) {
            return queryExecutor().lookupByPrimaryKey(dmlPojo.primaryKey())
                                  .map(p -> DMLPojo.builder()
                                                   .request(dmlPojo.request())
                                                   .primaryKey(dmlPojo.primaryKey())
                                                   .dbEntity((JsonRecord) p)
                                                   .build());
        }

    }

}

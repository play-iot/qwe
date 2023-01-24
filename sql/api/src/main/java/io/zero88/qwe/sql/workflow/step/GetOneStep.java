package io.zero88.qwe.sql.workflow.step;

import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.validation.OperationValidator;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
@SuppressWarnings("unchecked")
public final class GetOneStep<T extends JsonRecord> extends AbstractSQLStep implements DQLStep<T> {

    @Override
    public Future<T> query(@NonNull RequestData reqData, @NonNull OperationValidator validator) {
        return queryExecutor().findOneByKey(reqData).flatMap(p -> validator.validate(reqData, (JsonRecord) p));
    }

}

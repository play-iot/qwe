package io.zero88.qwe.sql.workflow.step;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.jooq.Configuration;

import io.vertx.core.Future;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.sql.pojos.DMLPojo;
import io.zero88.qwe.sql.validation.OperationValidator;
import io.zero88.qwe.sql.workflow.step.DMLStep.CreateOrUpdateStep;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Accessors(fluent = true)
@SuperBuilder
@SuppressWarnings("unchecked")
public final class CreationStep extends AbstractSQLStep implements CreateOrUpdateStep {

    @Setter
    private BiConsumer<EventAction, DMLPojo> onSuccess;

    @Override
    public Future<DMLPojo> execute(@NonNull RequestData requestData, @NonNull OperationValidator validator,
                                   Configuration configuration) {
        final Future<DMLPojo> result = queryExecutor().runtimeConfiguration(configuration)
                                                      .insertReturningPrimary(requestData, validator)
                                                      .flatMap(pojo -> lookup((DMLPojo) pojo));
        if (Objects.nonNull(onSuccess)) {
            return result.onSuccess(keyPojo -> onSuccess.accept(action(), keyPojo));
        }
        return result;
    }

}

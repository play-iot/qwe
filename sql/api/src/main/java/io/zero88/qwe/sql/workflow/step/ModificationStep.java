package io.zero88.qwe.sql.workflow.step;

import java.util.Objects;

import org.jooq.Configuration;

import io.github.zero88.utils.TripleConsumer;
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
@SuperBuilder
@Accessors(fluent = true)
@SuppressWarnings("unchecked")
public final class ModificationStep extends AbstractSQLStep implements CreateOrUpdateStep {

    @Setter
    private TripleConsumer<RequestData, EventAction, DMLPojo> onSuccess;

    @Override
    public Future<DMLPojo> execute(@NonNull RequestData requestData, @NonNull OperationValidator validator,
                                   Configuration configuration) {
        final Future<DMLPojo> result = queryExecutor().runtimeConfiguration(configuration)
                                                      .modifyReturningPrimary(requestData, validator)
                                                      .flatMap(dmlPojo -> lookup((DMLPojo) dmlPojo));
        if (Objects.nonNull(onSuccess)) {
            return result.onSuccess(keyPojo -> onSuccess.accept(requestData, action(), keyPojo));
        }
        return result;
    }

}

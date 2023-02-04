package cloud.playio.qwe.sql.workflow.step;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.jooq.Configuration;

import io.vertx.core.Future;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.sql.pojos.DMLPojo;
import cloud.playio.qwe.sql.validation.OperationValidator;
import cloud.playio.qwe.sql.workflow.step.DMLStep.CreateOrUpdateStep;

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

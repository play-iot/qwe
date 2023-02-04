package cloud.playio.qwe.sql.workflow.step;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.jooq.Configuration;

import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.sql.pojos.DMLPojo;
import cloud.playio.qwe.sql.validation.OperationValidator;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
@SuppressWarnings("unchecked")
public final class DeletionStep extends AbstractSQLStep implements DMLStep {

    @Setter
    private BiConsumer<EventAction, DMLPojo> onSuccess;

    @Override
    public Future<DMLPojo> execute(@NonNull RequestData requestData, @NonNull OperationValidator validator,
                                   Configuration configuration) {
        final Future<DMLPojo> result = queryExecutor().runtimeConfiguration(configuration)
                                                      .deleteOneByKey(requestData, validator)
                                                      .map(p -> DMLPojo.builder().dbEntity((JsonRecord) p).build());
        if (Objects.nonNull(onSuccess)) {
            return result.onSuccess(keyPojo -> onSuccess.accept(action(), keyPojo));
        }
        return result;
    }

}

package cloud.playio.qwe.sql.workflow;

import java.util.function.BiFunction;

import org.jooq.Configuration;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.workflow.step.DQLBatchStep;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class DefaultDQLBatchWorkflow extends AbstractSQLWorkflow implements DQLBatchWorkflow {

    @NonNull
    private final DQLBatchStep sqlStep;
    @NonNull
    private final BiFunction<RequestData, JsonArray, Future<JsonObject>> transformer;

    @Override
    protected @NonNull Future<JsonObject> run(@NonNull RequestData requestData, Configuration runtimeConfig) {
        final RequestData reqData = normalize().apply(requestData);
        return sqlStep().query(reqData, validator().andThen(afterValidation()))
                        .flatMap(pojo -> transformer().apply(reqData, pojo));
    }

}

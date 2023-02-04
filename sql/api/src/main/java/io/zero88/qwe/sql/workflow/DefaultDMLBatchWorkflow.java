package cloud.playio.qwe.sql.workflow;

import org.jooq.Configuration;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.workflow.step.DMLBatchStep;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class DefaultDMLBatchWorkflow extends AbstractSQLWorkflow implements DMLBatchWorkflow {

    @NonNull
    private final DMLBatchStep sqlStep;
    private final boolean continueOnError;

    @Override
    protected @NonNull Future<JsonObject> run(@NonNull RequestData requestData, Configuration runtimeConfig) {
        return Future.failedFuture(new UnsupportedOperationException("Not yet implemented"));
    }

}

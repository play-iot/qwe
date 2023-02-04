package cloud.playio.qwe.sql.workflow;

import java.util.function.BiFunction;

import org.jooq.Configuration;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.pojos.DMLPojo;
import cloud.playio.qwe.sql.workflow.step.DMLStep;
import cloud.playio.qwe.sql.workflow.task.EntityTaskExecuter.AsyncEntityTaskExecuter;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public class DefaultDMLWorkflow<T extends DMLStep> extends AbstractSQLWorkflow implements DMLWorkflow {

    @NonNull
    private final T sqlStep;
    @NonNull
    private final BiFunction<RequestData, DMLPojo, Future<JsonObject>> transformer;

    @Override
    protected @NonNull Future<JsonObject> run(@NonNull RequestData requestData, Configuration runtimeConfig) {
        final RequestData reqData = normalize().apply(requestData);
        final AsyncEntityTaskExecuter postAsyncExecuter = taskManager().postAsyncExecuter();
        return sqlStep().execute(reqData, validator().andThen(afterValidation()), runtimeConfig)
                        .flatMap(dmlPojo -> taskManager().postBlockingExecuter()
                                                         .execute(initSuccessData(reqData, dmlPojo.dbEntity()))
                                                         .map(pojo -> DMLPojo.clone(dmlPojo, pojo))
                                                         .otherwise(dmlPojo))
                        .onSuccess(db -> postAsyncExecuter.execute(initSuccessData(reqData, db.dbEntity())))
                        .onFailure(err -> postAsyncExecuter.execute(initErrorData(reqData, err)))
                        .flatMap(pojo -> transformer().apply(reqData, pojo));
    }

}

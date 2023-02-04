package cloud.playio.qwe.sql.workflow.task;

import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.query.EntityQueryExecutor;
import cloud.playio.qwe.sql.service.cache.EntityServiceIndex;

import lombok.NonNull;

public interface PurgeDefinitionContext extends EntityDefinitionContext {

    static PurgeDefinitionContext create(@NonNull EntityQueryExecutor queryExecutor) {
        return () -> queryExecutor;
    }

    @NonNull EntityQueryExecutor queryExecutor();

    @Override
    default @NonNull EntityHandler entityHandler() {
        return queryExecutor().entityHandler();
    }

    @NonNull
    default EntityServiceIndex entityServiceIndex() {
        return entityHandler().sharedData().getData(EntityServiceIndex.DATA_KEY);
    }

}

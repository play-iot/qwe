package cloud.playio.qwe.sql.workflow.step;

import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.sql.query.EntityQueryExecutor;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
abstract class AbstractSQLStep implements SQLStep {

    @NonNull
    private final EventAction action;
    @NonNull
    private final EntityQueryExecutor queryExecutor;

}

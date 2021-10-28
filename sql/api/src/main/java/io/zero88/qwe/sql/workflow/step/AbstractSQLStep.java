package io.zero88.qwe.sql.workflow.step;

import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.sql.query.EntityQueryExecutor;

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

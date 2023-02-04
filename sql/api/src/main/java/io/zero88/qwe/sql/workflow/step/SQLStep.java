package cloud.playio.qwe.sql.workflow.step;

import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.sql.query.EntityQueryExecutor;
import cloud.playio.qwe.workflow.Workflow;

import lombok.NonNull;

/**
 * Represents a direct execution step into/onto database
 *
 * @see DMLStep
 * @see DQLStep
 * @see SQLBatchStep
 * @since 1.0.0
 */
public interface SQLStep extends Workflow {

    /**
     * Declares event action.
     *
     * @return the event action
     * @since 1.0.0
     */
    @NonNull EventAction action();

    /**
     * Declares entity query executor.
     *
     * @return the entity query executor
     * @see EntityQueryExecutor
     * @since 1.0.0
     */
    @NonNull EntityQueryExecutor queryExecutor();

}

package io.zero88.qwe.sql.workflow;

import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.sql.workflow.step.DQLStep;

import lombok.NonNull;

/**
 * DML workflow is used for querying one data from database.
 *
 * @param <T> Type of {@code JsonRecord}
 * @see SQLWorkflow
 * @since 1.0.0
 */
public interface DQLWorkflow<T extends JsonRecord> extends SQLWorkflow {

    /**
     * Declares {@code DQL step}
     *
     * @return the DQL step
     * @since 1.0.0
     */
    @NonNull DQLStep<T> sqlStep();

}

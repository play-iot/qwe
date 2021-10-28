package io.zero88.qwe.sql.workflow;

import io.zero88.qwe.sql.workflow.step.DMLStep;

import lombok.NonNull;

/**
 * DML workflow is used for adding (inserting), modifying (updating), and deleting data into database.
 *
 * @see SQLWorkflow
 * @since 1.0.0
 */
public interface DMLWorkflow extends SQLWorkflow {

    /**
     * Declares {@code DML step}
     *
     * @return the DML step
     * @since 1.0.0
     */
    @NonNull DMLStep sqlStep();

}

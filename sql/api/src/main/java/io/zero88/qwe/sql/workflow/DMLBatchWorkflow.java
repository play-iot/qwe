package io.zero88.qwe.sql.workflow;

import io.zero88.qwe.sql.workflow.step.DMLBatchStep;

import lombok.NonNull;

/**
 * DML Batch workflow is used for adding (inserting), modifying (updating), and deleting data in batch into database.
 *
 * @see SQLWorkflow
 * @since 1.0.0
 */
public interface DMLBatchWorkflow extends SQLWorkflow, SQLTransactionWorkflow {

    /**
     * Declares {@code DML Batch step}
     *
     * @return the DML step
     * @see DMLBatchStep
     * @since 1.0.0
     */
    @NonNull DMLBatchStep sqlStep();

    @Override
    boolean continueOnError();

}

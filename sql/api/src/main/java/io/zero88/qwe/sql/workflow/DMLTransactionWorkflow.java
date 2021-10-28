package io.zero88.qwe.sql.workflow;

public interface DMLTransactionWorkflow extends DMLWorkflow, SQLTransactionWorkflow {

    @Override
    default boolean continueOnError() {
        return false;
    }

}

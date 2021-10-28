package io.zero88.qwe.sql.workflow;

/**
 * The interface Sql transaction workflow.
 *
 * @since 1.0.0
 */
public interface SQLTransactionWorkflow {

    /**
     * Declares {@code SQL transaction workflow} still continue the execution though catching an error
     *
     * @return {@code true} if {@code SQL transaction} still run in case of catching any error
     * @apiNote It is useful only {@code SQL batch} execution
     * @since 1.0.0
     */
    boolean continueOnError();

}

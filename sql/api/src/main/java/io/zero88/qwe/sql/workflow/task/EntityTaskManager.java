package io.zero88.qwe.sql.workflow.task;

import io.zero88.qwe.sql.workflow.task.EntityTaskExecuter.AsyncEntityTaskExecuter;
import io.zero88.qwe.sql.workflow.task.EntityTaskExecuter.BlockingEntityTaskExecuter;

/**
 * The interface Entity task manager.
 *
 * @since 1.0.0
 */
public interface EntityTaskManager {

    /**
     * The constant EMPTY.
     */
    EntityTaskManager EMPTY = new EntityTaskManager() {};

    /**
     * Defines {@code blocking pre-task} is run before the entity manager do query or persist
     *
     * @return pre blocking task
     * @see EntityTask
     * @since 1.0.0
     */
    default EntityTask prePersistTask() {
        return null;
    }

    /**
     * Defines {@code blocking post-task} is run after the entity manager do query or persist
     *
     * @return post blocking task
     * @see EntityTask
     * @since 1.0.0
     */
    default EntityTask postPersistTask() {
        return null;
    }

    /**
     * Defines {@code async post-task} is run after the entity manager do query or persist
     *
     * @return post async task
     * @see EntityTask
     * @since 1.0.0
     */
    default EntityTask postPersistAsyncTask() {
        return null;
    }

    /**
     * Creates pre-blocking-task executer.
     * <p>
     * It is called after {@code validating} and before {@code executing SQL}.
     *
     * @return the pre-blocking-task executer
     * @see #prePersistTask() #prePersistTask()
     * @since 1.0.0
     */
    default BlockingEntityTaskExecuter preBlockingExecuter() {
        return BlockingEntityTaskExecuter.create(prePersistTask());
    }

    /**
     * Creates post-blocking-task executer.
     * <p>
     * It is called after {@code executing SQL}. It will not invoke {@code blocking post-task} if any error in {@code
     * SQL execution}* phase.
     *
     * @return the post-blocking-task executer
     * @see #postPersistTask() #postPersistTask()
     * @since 1.0.0
     */
    default BlockingEntityTaskExecuter postBlockingExecuter() {
        return BlockingEntityTaskExecuter.create(postPersistTask());
    }

    /**
     * Creates post-async-task executer.
     * <p>
     * It is invoked {@code async post-task} regardless {@code SQL execution} phase is success or error.
     *
     * @return the post-async-task executer
     * @see #postPersistAsyncTask() #postPersistAsyncTask()
     * @since 1.0.0
     */
    default AsyncEntityTaskExecuter postAsyncExecuter() {
        return AsyncEntityTaskExecuter.create(postPersistAsyncTask());
    }

}

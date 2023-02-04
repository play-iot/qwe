package cloud.playio.qwe.workflow;

import java.util.Objects;

/**
 * Represents Task execution data, aka the runtime task data in execution phase.
 *
 * @param <D> Type of {@code data}
 * @since 1.0.0
 */
public interface TaskExecutionContext<D> extends TaskContext {

    /**
     * Gets data.
     *
     * @return the data
     * @since 1.0.0
     */
    D getData();

    /**
     * Gets throwable.
     *
     * @return the throwable
     * @since 1.0.0
     */
    Throwable getThrowable();

    /**
     * Is error.
     *
     * @return {@code true} if {@link #getThrowable()} is {@code non-null}
     * @since 1.0.0
     */
    default boolean isError() {
        return Objects.nonNull(getThrowable());
    }

}

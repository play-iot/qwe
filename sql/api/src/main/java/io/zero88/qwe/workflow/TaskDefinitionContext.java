package cloud.playio.qwe.workflow;

import io.vertx.core.Vertx;

import lombok.NonNull;

/**
 * Represents {@code Task definition context}.
 *
 * @since 1.0.0
 */
public interface TaskDefinitionContext extends TaskContext {

    /**
     * Vertx
     *
     * @return vertx instance
     * @since 1.0.0
     */
    @NonNull Vertx vertx();

    /**
     * Define {@code task} will be executed in {@code another worker thread} or {@code same thread} with current
     * thread.
     *
     * @return {@code true} if {@code task} will be executed in {@code another worker}
     * @since 1.0.0
     */
    boolean isConcurrent();

}

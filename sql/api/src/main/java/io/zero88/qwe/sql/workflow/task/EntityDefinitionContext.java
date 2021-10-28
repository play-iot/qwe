package io.zero88.qwe.sql.workflow.task;

import io.vertx.core.Vertx;
import io.zero88.qwe.sql.handler.EntityHandler;
import io.zero88.qwe.workflow.TaskDefinitionContext;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents Entity task definition context.
 *
 * @see TaskDefinitionContext
 * @since 1.0.0
 */
public interface EntityDefinitionContext extends TaskDefinitionContext {

    /**
     * Create entity definition context.
     *
     * @param entityHandler the entity handler
     * @return the entity definition context
     * @since 1.0.0
     */
    static EntityDefinitionContext create(@NonNull EntityHandler entityHandler) {
        return new DefaultEntityDefinitionContext(entityHandler);
    }

    /**
     * Defines entity handler.
     *
     * @return the entity handler
     * @see EntityHandler
     * @since 1.0.0
     */
    @NonNull EntityHandler entityHandler();

    @Override
    default @NonNull Vertx vertx() {
        return entityHandler().vertx();
    }

    @Override
    default boolean isConcurrent() {
        return true;
    }

    /**
     * Default entity definition context.
     *
     * @since 1.0.0
     */
    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class DefaultEntityDefinitionContext implements EntityDefinitionContext {

        private final EntityHandler entityHandler;

    }

}

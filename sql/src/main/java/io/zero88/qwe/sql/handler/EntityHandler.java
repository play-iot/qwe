package io.zero88.qwe.sql.handler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Catalog;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.zero88.jooqx.JooqDSLProvider;
import io.zero88.jooqx.LegacyJooqx;
import io.zero88.jooqx.ReactiveJooqx;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.HasSharedData;
import io.zero88.qwe.PluginContext;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventBusProxy;
import io.zero88.qwe.sql.SQLPluginConfig;
import io.zero88.qwe.sql.schema.SchemaHandler;

import lombok.NonNull;

/**
 * Represents for Entity handler.
 *
 * @since 1.0.0
 */
public interface EntityHandler<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                  E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends HasSharedData, EventBusProxy, HasLogger, JooqDSLProvider {

    @Override
    default Logger logger() {
        return LoggerFactory.getLogger(EntityHandler.class);
    }

    @Override
    default EventBusClient transporter() {
        return EventBusClient.create(sharedData());
    }

    /**
     * Setup Entity handler
     *
     * @param sharedData        shared data
     * @param pluginContext     Pre-plugin context
     * @param pluginConfig      SQL plugin config
     * @param jooqxExtensionCls jooqx extension class
     * @return a reference future to this for fluent API
     * @see JooqxExtension
     */
    Future<EntityHandler<S, B, PQ, RS, RC, E>> setup(@NotNull SharedDataLocalProxy sharedData,
                                                     @NonNull PluginContext pluginContext,
                                                     @NotNull SQLPluginConfig pluginConfig,
                                                     @Nullable Class<JooqxExtension<S, B, PQ, RS, RC, E>> jooqxExtensionCls);

    /**
     * jOOQx instance
     *
     * @return jOOQx instance
     * @see LegacyJooqx
     * @see ReactiveJooqx
     */
    E jooqx();

    @Override
    default DSLContext dsl() {
        return jooqx().dsl();
    }

    /**
     * Defines database catalog.
     *
     * @return the catalog
     * @since 1.0.0
     */
    @NonNull Catalog catalog();

    /**
     * Get {@code Schema handler}.
     *
     * @return the schema handler
     * @see SchemaHandler
     * @since 1.0.0
     */
    @NonNull SchemaHandler<S, B, PQ, RS, RC, E> schemaHandler();

    //    /**
    //     * Gets {@code entity constraint holder}.
    //     *
    //     * @return the entity constraint holder. Default is {@link EntityConstraintHolder#BLANK}
    //     * @see EntityConstraintHolder
    //     * @since 1.0.0
    //     */
    //    default @NonNull EntityConstraintHolder holder() {
    //        return EntityConstraintHolder.BLANK;
    //    }

    //    /**
    //     * Gets {@code Metadata index}.
    //     *
    //     * @return the metadata index. Default is {@link MetadataIndex#BLANK}
    //     * @see MetadataIndex
    //     * @since 1.0.0
    //     */
    //    default @NonNull MetadataIndex metadataIndex() {
    //        return MetadataIndex.BLANK;
    //    }
}

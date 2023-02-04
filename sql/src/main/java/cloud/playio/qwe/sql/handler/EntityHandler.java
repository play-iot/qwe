package cloud.playio.qwe.sql.handler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Catalog;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.github.zero88.jooqx.JooqDSLProvider;
import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import cloud.playio.qwe.HasSharedData;
import cloud.playio.qwe.PluginContext;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.eventbus.EventBusProxy;
import cloud.playio.qwe.sql.SQLLogSystem;
import cloud.playio.qwe.sql.SQLPluginConfig;
import cloud.playio.qwe.sql.handler.schema.SchemaHandler;

import lombok.NonNull;

/**
 * Represents for Entity handler.
 *
 * @since 1.0.0
 */
public interface EntityHandler<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                  E extends SQLExecutor<S, B, PQ, RC>>
    extends HasSharedData, EventBusProxy, JooqDSLProvider, SQLLogSystem {

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
     * @see JooqxBaseExtension
     */
    Future<EntityHandler<S, B, PQ, RC, E>> setup(@NotNull SharedDataLocalProxy sharedData,
                                                 @NonNull PluginContext pluginContext,
                                                 @NotNull SQLPluginConfig pluginConfig,
                                                 @Nullable Class<JooqxBaseExtension<S, B, PQ, RC, E>> jooqxExtensionCls);

    /**
     * jOOQx instance
     *
     * @return jOOQx instance
     * @see Jooqx
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
    @NonNull SchemaHandler<S, B, PQ, RC, E> schemaHandler();

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

package cloud.playio.qwe.sql.handler;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.JooqDSLProvider;
import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import cloud.playio.qwe.PluginContext;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.exceptions.InitializerError;
import cloud.playio.qwe.sql.SQLPluginConfig;
import cloud.playio.qwe.sql.spi.extension.JooqxExtensionRegistry;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Represents for Abstract entity handler.
 *
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
public abstract class EntityHandlerImpl<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                           E extends SQLExecutor<S, B, PQ, RC>>
    implements EntityHandler<S, B, PQ, RC, E> {

    private SharedDataLocalProxy sharedData;
    private E jooqx;

    @Override
    public Future<EntityHandler<S, B, PQ, RC, E>> setup(@NotNull SharedDataLocalProxy sharedData,
                                                        @NotNull PluginContext pluginContext,
                                                        @NotNull SQLPluginConfig pluginConfig,
                                                        @Nullable Class<JooqxBaseExtension<S, B, PQ, RC, E>> jooqxExtensionCls) {
        this.sharedData = sharedData;
        return Functions.getOrThrow(() -> Objects.requireNonNull(createExtension(pluginConfig, jooqxExtensionCls)),
                                    t -> new InitializerError("Unable create jOOQx Extension"))
                        .setup(pluginContext, pluginConfig)
                        .jooqx(sharedData.getVertx(), createDSL(pluginConfig), pluginConfig.connectionOptions(),
                               pluginConfig.poolOptions())
                        .onSuccess(jooqx -> this.jooqx = jooqx)
                        .map(ignore -> this);
    }

    @SuppressWarnings("unchecked")
    protected JooqxBaseExtension<S, B, PQ, RC, E> createExtension(@NotNull SQLPluginConfig pluginConfig,
                                                                  Class<JooqxBaseExtension<S, B, PQ, RC, E>> jooqxExtCls) {
        JooqxBaseExtension<S, B, PQ, RC, E> extension = null;
        if (Strings.isNotBlank(pluginConfig.getJooqxExtensionClass())) {
            logger().debug("Load jOOQx Extension from config[{}]...", pluginConfig.getJooqxExtensionClass());
            extension = ReflectionClass.createObject(pluginConfig.getJooqxExtensionClass());
        }
        if (Objects.isNull(extension) && Objects.nonNull(jooqxExtCls)) {
            logger().debug("Load jOOQx Extension from class[{}]...", jooqxExtCls.getName());
            extension = ReflectionClass.createObject(jooqxExtCls);
        }
        if (Objects.isNull(extension) && pluginConfig.isAutoDetect()) {
            logger().debug("Auto detect jOOQx Extension on[{}]", pluginConfig.getDialect());
            extension = JooqxExtensionRegistry.lookup(pluginConfig.getDialect());
        }
        return extension;
    }

    protected @NotNull DSLContext createDSL(@NotNull SQLPluginConfig pluginConfig) {
        return JooqDSLProvider.create(pluginConfig.getDialect()).dsl();
    }

}

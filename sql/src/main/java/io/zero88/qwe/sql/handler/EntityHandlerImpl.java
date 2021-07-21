package io.zero88.qwe.sql.handler;

import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Functions;
import io.vertx.core.Future;
import io.zero88.jooqx.JooqDSLProvider;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.qwe.PluginContext;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.sql.SQLPluginConfig;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Represents for Abstract entity handler.
 *
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
public abstract class EntityHandlerImpl<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                           E extends SQLExecutor<S, B, PQ, RS, RC>>
    implements EntityHandler<S, B, PQ, RS, RC, E> {

    private SharedDataLocalProxy sharedData;
    private E jooqx;

    @Override
    public Future<EntityHandler<S, B, PQ, RS, RC, E>> setup(@NotNull SharedDataLocalProxy sharedData,
                                                            @NonNull Class<JooqxExtension<S, B, PQ, RS, RC, E>> jooqxExtensionCls,
                                                            @NotNull SQLPluginConfig pluginConfig,
                                                            @NotNull PluginContext pluginContext) {
        this.sharedData = sharedData;
        return Functions.getOrThrow(() -> Objects.requireNonNull(createExtension(jooqxExtensionCls, pluginConfig)),
                                    t -> new InitializerError("Unable create jOOQx extension", t))
                        .setup(pluginContext)
                        .jooqx(sharedData.getVertx(), createDSL(pluginConfig), pluginConfig.connectionOptions(),
                               pluginConfig.poolOptions())
                        .onSuccess(jooqx -> this.jooqx = jooqx)
                        .map(ignore -> this);
    }

    @SuppressWarnings("unchecked")
    protected JooqxExtension<S, B, PQ, RS, RC, E> createExtension(
        @NotNull Class<JooqxExtension<S, B, PQ, RS, RC, E>> facadeCls, @NotNull SQLPluginConfig pluginConfig) {
        return (JooqxExtension<S, B, PQ, RS, RC, E>) Optional.ofNullable(pluginConfig.getJooqxFacadeClass())
                                                             .map(ReflectionClass::createObject)
                                                             .orElseGet(() -> ReflectionClass.createObject(facadeCls));
    }

    protected @NotNull DSLContext createDSL(@NotNull SQLPluginConfig pluginConfig) {
        return JooqDSLProvider.create(pluginConfig.getDialect()).dsl();
    }

}

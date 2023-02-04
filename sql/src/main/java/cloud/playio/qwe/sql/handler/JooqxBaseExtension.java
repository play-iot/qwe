package cloud.playio.qwe.sql.handler;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import io.github.zero88.jooqx.provider.BaseJooqxFacade;
import io.github.zero88.jooqx.provider.BaseJooqxProvider;
import io.github.zero88.jooqx.provider.SQLClientProvider;
import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.PluginContext;
import cloud.playio.qwe.sql.SQLPluginConfig;

/**
 * Represents for jOOQx extension that makes compatible between {@code JooqxFacade} with {@code PluginContext}
 *
 * @see PluginContext
 * @see BaseJooqxFacade
 * @see BaseJooqxProvider
 * @see SQLClientProvider
 */
public interface JooqxBaseExtension<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                       E extends SQLExecutor<S, B, PQ, RC>>
    extends BaseJooqxFacade<S, B, PQ, RC, E>, SQLClientProvider<S>, BaseJooqxProvider<S, B, PQ, RC, E>, HasLogger {

    @Override
    default Logger logger() {
        return LoggerFactory.getLogger(JooqxBaseExtension.class);
    }

    @Override
    default @NotNull SQLClientProvider<S> clientProvider() {
        return this;
    }

    @Override
    default @NotNull BaseJooqxProvider<S, B, PQ, RC, E> jooqxProvider() {
        return this;
    }

    /**
     * Setup jooqx extension
     *
     * @param pluginContext SQL plugin context
     * @param pluginConfig  SQL plugin config
     * @return a reference to this for fluent API
     */
    @NotNull
    default JooqxBaseExtension<S, B, PQ, RC, E> setup(@NotNull PluginContext pluginContext,
                                                      @NotNull SQLPluginConfig pluginConfig) {
        return this;
    }

}

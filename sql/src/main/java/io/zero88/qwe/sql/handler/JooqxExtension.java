package io.zero88.qwe.sql.handler;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.LegacyJooqx;
import io.zero88.jooqx.LegacySQLCollector;
import io.zero88.jooqx.LegacySQLPreparedQuery;
import io.zero88.jooqx.ReactiveJooqxBase;
import io.zero88.jooqx.ReactiveSQLPreparedQuery;
import io.zero88.jooqx.ReactiveSQLResultCollector;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.provider.JooqxFacade;
import io.zero88.jooqx.provider.JooqxProvider;
import io.zero88.jooqx.provider.LegacyJooqxProvider;
import io.zero88.jooqx.provider.ReactiveJooqxProvider;
import io.zero88.jooqx.provider.SQLClientProvider;
import io.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.PluginContext;

/**
 * Represents for jOOQx extension that makes compatible between {@code JooqxFacade} with {@code PluginContext}
 *
 * @see JooqxFacade
 * @see PluginContext
 * @see SQLClientProvider
 * @see JooqxProvider
 */
public interface JooqxExtension<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                   E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends JooqxFacade<S, B, PQ, RS, RC, E>, SQLClientProvider<S>, JooqxProvider<S, B, PQ, RS, RC, E>, HasLogger {

    @Override
    default Logger logger() {
        return LoggerFactory.getLogger(JooqxExtension.class);
    }

    @Override
    default @NotNull SQLClientProvider<S> clientProvider() {
        return this;
    }

    @Override
    default @NotNull JooqxProvider<S, B, PQ, RS, RC, E> jooqxProvider() {
        return this;
    }

    /**
     * Setup jooqx extension
     *
     * @param pluginContext plugin context
     * @return a reference to this for fluent API
     */
    @NotNull
    default JooqxExtension<S, B, PQ, RS, RC, E> setup(@NotNull PluginContext pluginContext) {
        return this;
    }

    /**
     * Represents for jOOQx reactive extension
     *
     * @param <S> Type of reactive SQL client
     * @see SQLClient
     * @see JooqxReactiveFacade
     * @see ReactiveJooqxProvider
     */
    interface JooqxReactiveExtension<S extends SqlClient> extends JooqxReactiveFacade<S>, ReactiveJooqxProvider<S>,
                                                                  JooqxExtension<S, Tuple, ReactiveSQLPreparedQuery,
                                                                                    RowSet<Row>,
                                                                                    ReactiveSQLResultCollector,
                                                                                    ReactiveJooqxBase<S>> {

    }


    /**
     * Represents for jOOQx legacy extension
     *
     * @see JooqxLegacyFacade
     * @see LegacyJooqxProvider
     * @see JDBCErrorConverterProvider
     */
    interface JooqxLegacyExtension extends JooqxLegacyFacade, LegacyJooqxProvider, JDBCErrorConverterProvider,
                                           JooqxExtension<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet,
                                                             LegacySQLCollector, LegacyJooqx> {

    }

}

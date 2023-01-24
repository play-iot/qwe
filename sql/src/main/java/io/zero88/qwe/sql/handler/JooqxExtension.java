package io.zero88.qwe.sql.handler;

import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.github.zero88.jooqx.JooqxBase;
import io.github.zero88.jooqx.JooqxPreparedQuery;
import io.github.zero88.jooqx.JooqxResultCollector;
import io.github.zero88.jooqx.provider.JooqxFacade;
import io.github.zero88.jooqx.provider.JooqxProvider;
import io.github.zero88.jooqx.provider.JooqxSQLClientProvider;

/**
 * Represents for jOOQx reactive extension
 *
 * @param <S> Type of reactive SQL client
 * @see SQLClient
 * @see JooqxFacade
 * @see JooqxProvider
 * @see JooqxSQLClientProvider
 */
public interface JooqxExtension<S extends SqlClient>
    extends JooqxFacade<S>, JooqxProvider<S>, JooqxSQLClientProvider<S>,
            JooqxBaseExtension<S, Tuple, JooqxPreparedQuery, JooqxResultCollector, JooqxBase<S>> {

}

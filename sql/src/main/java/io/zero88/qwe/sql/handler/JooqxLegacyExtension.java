package io.zero88.qwe.sql.handler;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.zero88.jooqx.LegacyJooqx;
import io.zero88.jooqx.LegacySQLCollector;
import io.zero88.jooqx.LegacySQLPreparedQuery;
import io.zero88.jooqx.provider.LegacyJooqxFacade;
import io.zero88.jooqx.provider.LegacyJooqxProvider;
import io.zero88.jooqx.provider.LegacySQLClientProvider;
import io.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;

/**
 * Represents for jOOQx legacy extension
 *
 * @see LegacyJooqxFacade
 * @see LegacyJooqxProvider
 * @see JDBCErrorConverterProvider
 */
public interface JooqxLegacyExtension<P extends DataSourceProvider>
    extends LegacyJooqxFacade, LegacyJooqxProvider, LegacySQLClientProvider<P>, JDBCErrorConverterProvider,
            JooqxBaseExtension<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector, LegacyJooqx> {

}

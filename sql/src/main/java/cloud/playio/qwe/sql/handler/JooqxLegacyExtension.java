package cloud.playio.qwe.sql.handler;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.sql.SQLClient;
import io.github.zero88.jooqx.LegacyJooqx;
import io.github.zero88.jooqx.LegacySQLCollector;
import io.github.zero88.jooqx.LegacySQLPreparedQuery;
import io.github.zero88.jooqx.provider.LegacyJooqxFacade;
import io.github.zero88.jooqx.provider.LegacyJooqxProvider;
import io.github.zero88.jooqx.provider.LegacySQLClientProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;

/**
 * Represents for jOOQx legacy extension
 *
 * @see LegacyJooqxFacade
 * @see LegacyJooqxProvider
 * @see JDBCErrorConverterProvider
 */
public interface JooqxLegacyExtension<P extends DataSourceProvider>
    extends LegacyJooqxFacade, LegacyJooqxProvider, LegacySQLClientProvider<P>, JDBCErrorConverterProvider,
            JooqxBaseExtension<SQLClient, JsonArray, LegacySQLPreparedQuery, LegacySQLCollector, LegacyJooqx> {

}

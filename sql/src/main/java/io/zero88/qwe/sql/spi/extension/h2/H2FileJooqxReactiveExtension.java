package io.zero88.qwe.sql.spi.extension.h2;

import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.ReactiveJooqxBase;
import io.zero88.jooqx.ReactiveSQLPreparedQuery;
import io.zero88.jooqx.ReactiveSQLResultCollector;
import io.zero88.jooqx.spi.h2.H2DBFileProvider;
import io.zero88.qwe.sql.spi.DBFileJooqxExtension;
import io.zero88.qwe.sql.spi.extension.jdbc.JDBCJooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for {@code H2} local file via {@code JDBCPool}
 *
 * @see JDBCPool
 * @see JDBCJooqxReactiveExtension
 * @see H2DBFileProvider
 */
public class H2FileJooqxReactiveExtension extends DBFileJooqxExtension<JDBCPool, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>,
                                                             ReactiveSQLResultCollector, ReactiveJooqxBase<JDBCPool>>
    implements JDBCJooqxReactiveExtension, H2DBFileProvider {

}

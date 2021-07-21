package io.zero88.qwe.sql.spi.extension.sqlite;

import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.ReactiveJooqxBase;
import io.zero88.jooqx.ReactiveSQLPreparedQuery;
import io.zero88.jooqx.ReactiveSQLResultCollector;
import io.zero88.jooqx.spi.sqlite.SQLiteDBFileProvider;
import io.zero88.qwe.sql.spi.DBFileJooqxExtension;
import io.zero88.qwe.sql.spi.extension.jdbc.JDBCJooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for {@code SQLite} local file via {@code JDBCPool}
 *
 * @see JDBCPool
 * @see JDBCJooqxReactiveExtension
 * @see SQLiteDBFileProvider
 */
public class SQLiteFileJooqxReactiveExtension extends DBFileJooqxExtension<JDBCPool, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>,
                                                                 ReactiveSQLResultCollector,
                                                                 ReactiveJooqxBase<JDBCPool>>
    implements JDBCJooqxReactiveExtension, SQLiteDBFileProvider {

}

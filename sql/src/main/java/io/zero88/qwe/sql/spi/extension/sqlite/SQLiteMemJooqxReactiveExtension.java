package io.zero88.qwe.sql.spi.extension.sqlite;

import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.spi.sqlite.SQLiteDBMemProvider;
import io.zero88.qwe.sql.spi.extension.jdbc.JDBCJooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for {@code SQLite} memory via {@code JDBCPool}
 *
 * @see JDBCPool
 * @see JDBCJooqxReactiveExtension
 * @see SQLiteDBMemProvider
 */
public interface SQLiteMemJooqxReactiveExtension extends JDBCJooqxReactiveExtension, SQLiteDBMemProvider {

}

package io.zero88.qwe.sql.spi.extension.jdbc;

import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.zero88.jooqx.spi.jdbc.JDBCPoolReactiveProvider;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for generic {@code JDBC pool}.
 * <p>
 * To use it, must add `io.vertx:vertx-sql-client` in your application classpath
 *
 * @see JDBCPool
 * @see JDBCPoolReactiveProvider
 * @see JDBCErrorConverterProvider
 * @see JooqxReactiveExtension
 */
public interface JDBCJooqxReactiveExtension
    extends JooqxReactiveExtension<JDBCPool>, JDBCPoolReactiveProvider, JDBCErrorConverterProvider {

}

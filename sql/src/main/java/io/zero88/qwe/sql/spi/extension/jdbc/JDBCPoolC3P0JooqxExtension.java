package io.zero88.qwe.sql.spi.extension.jdbc;

import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.zero88.jooqx.spi.jdbc.JDBCPoolC3P0Provider;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for generic {@code JDBC pool} that using {@code C3P0}
 *
 * @see JDBCPool
 * @see JDBCPoolC3P0Provider
 * @see JDBCErrorConverterProvider
 * @see JooqxReactiveExtension
 */
public class JDBCPoolC3P0JooqxExtension
    implements JooqxReactiveExtension<JDBCPool>, JDBCPoolC3P0Provider, JDBCErrorConverterProvider {

    public static final JDBCPoolC3P0JooqxExtension INSTANCE = new JDBCPoolC3P0JooqxExtension();

}

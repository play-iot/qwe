package io.zero88.qwe.sql.spi.extension.jdbc;

import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for generic {@code JDBC pool} that using {@code AgroalCP}
 *
 * @see JDBCPool
 * @see JDBCPoolHikariProvider
 * @see JDBCErrorConverterProvider
 * @see JooqxReactiveExtension
 */
public class JDBCPoolHikariJooqxExtension
    implements JooqxReactiveExtension<JDBCPool>, JDBCPoolHikariProvider, JDBCErrorConverterProvider {

    public static final JDBCPoolHikariJooqxExtension INSTANCE = new JDBCPoolHikariJooqxExtension();

}

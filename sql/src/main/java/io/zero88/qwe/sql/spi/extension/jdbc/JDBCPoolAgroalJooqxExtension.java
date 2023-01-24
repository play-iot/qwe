package io.zero88.qwe.sql.spi.extension.jdbc;

import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolAgroalProvider;
import io.vertx.jdbcclient.JDBCPool;
import io.zero88.qwe.sql.handler.JooqxExtension;

/**
 * QWE Jooqx reactive extension for generic {@code JDBC pool} that using {@code AgroalCP}
 * <p>
 * To use it, must add `io.vertx:vertx-sql-client` in your application classpath
 *
 * @see JDBCPool
 * @see JDBCPoolAgroalProvider
 * @see JDBCErrorConverterProvider
 * @see JooqxExtension
 */
public class JDBCPoolAgroalJooqxExtension
    implements JooqxExtension<JDBCPool>, JDBCPoolAgroalProvider, JDBCErrorConverterProvider {

    public static final JDBCPoolAgroalJooqxExtension INSTANCE = new JDBCPoolAgroalJooqxExtension();

}

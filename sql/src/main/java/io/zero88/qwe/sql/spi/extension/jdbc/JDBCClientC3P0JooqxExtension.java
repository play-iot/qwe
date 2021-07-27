package io.zero88.qwe.sql.spi.extension.jdbc;

import io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider;
import io.zero88.jooqx.spi.jdbc.JDBCLegacyC3P0Provider;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxLegacyExtension;

/**
 * QWE Jooqx legacy extension with {@code HikariCP} lib
 *
 * @see JooqxLegacyExtension
 * @see JDBCLegacyC3P0Provider
 */
public class JDBCClientC3P0JooqxExtension
    implements JooqxLegacyExtension<C3P0DataSourceProvider>, JDBCLegacyC3P0Provider {

}

package io.zero88.qwe.sql.spi.extension.jdbc;

import io.vertx.ext.jdbc.spi.impl.AgroalCPDataSourceProvider;
import io.zero88.jooqx.spi.jdbc.JDBCLegacyAgroalProvider;
import io.zero88.qwe.sql.handler.JooqxLegacyExtension;

/**
 * QWE Jooqx legacy extension with {@code HikariCP} lib
 *
 * @see JooqxLegacyExtension
 * @see JDBCLegacyAgroalProvider
 */
public class JDBCClientAgroalJooqxExtension
    implements JooqxLegacyExtension<AgroalCPDataSourceProvider>, JDBCLegacyAgroalProvider {

}

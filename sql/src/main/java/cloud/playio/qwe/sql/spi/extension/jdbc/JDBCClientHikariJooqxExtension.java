package cloud.playio.qwe.sql.spi.extension.jdbc;

import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCLegacyHikariProvider;
import cloud.playio.qwe.sql.handler.JooqxLegacyExtension;

/**
 * QWE Jooqx legacy extension with {@code HikariCP} lib
 *
 * @see JooqxLegacyExtension
 * @see JDBCLegacyHikariProvider
 */
public class JDBCClientHikariJooqxExtension
    implements JooqxLegacyExtension<HikariCPDataSourceProvider>, JDBCLegacyHikariProvider {

}

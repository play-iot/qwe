package cloud.playio.qwe.sql.spi.extension.jdbc;

import io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCLegacyC3P0Provider;
import cloud.playio.qwe.sql.handler.JooqxLegacyExtension;

/**
 * QWE Jooqx legacy extension with {@code HikariCP} lib
 *
 * @see JooqxLegacyExtension
 * @see JDBCLegacyC3P0Provider
 */
public class JDBCClientC3P0JooqxExtension
    implements JooqxLegacyExtension<C3P0DataSourceProvider>, JDBCLegacyC3P0Provider {

}

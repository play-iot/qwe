package cloud.playio.qwe.sql.spi.extension.jdbc;

import io.vertx.ext.jdbc.spi.impl.AgroalCPDataSourceProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCLegacyAgroalProvider;
import cloud.playio.qwe.sql.handler.JooqxLegacyExtension;

/**
 * QWE Jooqx legacy extension with {@code HikariCP} lib
 *
 * @see JooqxLegacyExtension
 * @see JDBCLegacyAgroalProvider
 */
public class JDBCClientAgroalJooqxExtension
    implements JooqxLegacyExtension<AgroalCPDataSourceProvider>, JDBCLegacyAgroalProvider {

}

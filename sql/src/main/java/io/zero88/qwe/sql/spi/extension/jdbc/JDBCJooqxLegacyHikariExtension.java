package io.zero88.qwe.sql.spi.extension.jdbc;

import io.zero88.jooqx.spi.jdbc.JDBCLegacyHikariProvider;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxLegacyExtension;

/**
 * QWE Jooqx legacy extension with {@code HikariCP} lib
 *
 * @see JooqxLegacyExtension
 * @see JDBCLegacyHikariProvider
 */
public interface JDBCJooqxLegacyHikariExtension extends JooqxLegacyExtension, JDBCLegacyHikariProvider {}

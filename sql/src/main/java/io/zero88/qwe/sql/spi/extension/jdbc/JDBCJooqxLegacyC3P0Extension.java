package io.zero88.qwe.sql.spi.extension.jdbc;

import io.zero88.jooqx.spi.jdbc.JDBCLegacyC3P0Provider;
import io.zero88.jooqx.spi.jdbc.JDBCLegacyHikariProvider;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxLegacyExtension;

/**
 * QWE Jooqx legacy extension with {@code HikariCP} lib
 *
 * @see JooqxLegacyExtension
 * @see JDBCLegacyC3P0Provider
 */
public interface JDBCJooqxLegacyC3P0Extension extends JooqxLegacyExtension, JDBCLegacyC3P0Provider {}

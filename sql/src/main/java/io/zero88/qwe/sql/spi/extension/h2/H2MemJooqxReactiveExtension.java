package io.zero88.qwe.sql.spi.extension.h2;

import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.spi.h2.H2DBMemProvider;
import io.zero88.qwe.sql.spi.extension.jdbc.JDBCJooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for {@code H2} memory via {@code JDBCPool}
 *
 * @see JDBCPool
 * @see JDBCJooqxReactiveExtension
 * @see H2DBMemProvider
 */
public interface H2MemJooqxReactiveExtension extends JDBCJooqxReactiveExtension, H2DBMemProvider {

}

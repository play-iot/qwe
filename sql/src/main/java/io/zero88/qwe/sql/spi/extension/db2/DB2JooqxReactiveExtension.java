package io.zero88.qwe.sql.spi.extension.db2;

import io.vertx.db2client.DB2Pool;
import io.zero88.jooqx.spi.db2.DB2ErrorConverterProvider;
import io.zero88.jooqx.spi.db2.DB2PoolProvider;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for {@code DB2}
 *
 * @see DB2Pool
 * @see DB2PoolProvider
 * @see DB2ErrorConverterProvider
 * @see JooqxReactiveExtension
 */
public interface DB2JooqxReactiveExtension
    extends JooqxReactiveExtension<DB2Pool>, DB2PoolProvider, DB2ErrorConverterProvider {

    DB2JooqxReactiveExtension INSTANCE = new DB2JooqxReactiveExtension() {};

}

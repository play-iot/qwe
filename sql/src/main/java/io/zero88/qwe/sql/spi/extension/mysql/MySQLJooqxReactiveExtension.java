package io.zero88.qwe.sql.spi.extension.mysql;

import io.vertx.mysqlclient.MySQLPool;
import io.zero88.jooqx.spi.mysql.MySQLErrorConverterProvider;
import io.zero88.jooqx.spi.mysql.MySQLPoolProvider;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for {@code MySQL}
 *
 * @see MySQLPool
 * @see MySQLPoolProvider
 * @see MySQLErrorConverterProvider
 * @see JooqxReactiveExtension
 */
public class MySQLJooqxReactiveExtension
    implements JooqxReactiveExtension<MySQLPool>, MySQLPoolProvider, MySQLErrorConverterProvider {

    public static final MySQLJooqxReactiveExtension INSTANCE = new MySQLJooqxReactiveExtension();

}

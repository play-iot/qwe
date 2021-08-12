package io.zero88.qwe.sql.spi.extension.mysql;

import io.vertx.mysqlclient.MySQLPool;
import io.zero88.jooqx.spi.mysql.MySQLErrorConverterProvider;
import io.zero88.jooqx.spi.mysql.MySQLPoolProvider;
import io.zero88.qwe.sql.handler.JooqxExtension;

/**
 * QWE Jooqx reactive extension for {@code MySQL}
 *
 * @see MySQLPool
 * @see MySQLPoolProvider
 * @see MySQLErrorConverterProvider
 * @see JooqxExtension
 */
public class MySQLJooqxExtension
    implements JooqxExtension<MySQLPool>, MySQLPoolProvider, MySQLErrorConverterProvider {

    public static final MySQLJooqxExtension INSTANCE = new MySQLJooqxExtension();

}

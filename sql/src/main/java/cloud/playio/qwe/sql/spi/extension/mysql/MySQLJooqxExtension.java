package cloud.playio.qwe.sql.spi.extension.mysql;

import io.vertx.mysqlclient.MySQLPool;
import io.github.zero88.jooqx.spi.mysql.MySQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.mysql.MySQLPoolProvider;
import cloud.playio.qwe.sql.handler.JooqxExtension;

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

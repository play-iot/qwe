package cloud.playio.qwe.sql.spi.extension.db2;

import io.github.zero88.jooqx.spi.db2.DB2ErrorConverterProvider;
import io.github.zero88.jooqx.spi.db2.DB2PoolProvider;
import io.vertx.db2client.DB2Pool;
import cloud.playio.qwe.sql.handler.JooqxExtension;

/**
 * QWE Jooqx reactive extension for {@code DB2}
 *
 * @see DB2Pool
 * @see DB2PoolProvider
 * @see DB2ErrorConverterProvider
 * @see JooqxExtension
 */
public class DB2JooqxExtension implements JooqxExtension<DB2Pool>, DB2PoolProvider, DB2ErrorConverterProvider {

    public static final DB2JooqxExtension INSTANCE = new DB2JooqxExtension();

}

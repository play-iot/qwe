package cloud.playio.qwe.sql.spi.extension.mssql;

import io.vertx.mssqlclient.MSSQLPool;
import io.github.zero88.jooqx.spi.mssql.MSSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.mssql.MSSQLPoolProvider;
import cloud.playio.qwe.sql.handler.JooqxExtension;

/**
 * QWE Jooqx reactive extension for {@code MSSQL}
 *
 * @see MSSQLPool
 * @see MSSQLPoolProvider
 * @see MSSQLErrorConverterProvider
 * @see JooqxExtension
 */
public class MSSQLJooqxExtension
    implements JooqxExtension<MSSQLPool>, MSSQLPoolProvider, MSSQLErrorConverterProvider {

    public static final MSSQLJooqxExtension INSTANCE = new MSSQLJooqxExtension();

}

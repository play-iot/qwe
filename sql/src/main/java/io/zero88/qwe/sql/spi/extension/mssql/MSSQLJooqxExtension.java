package io.zero88.qwe.sql.spi.extension.mssql;

import io.vertx.mssqlclient.MSSQLPool;
import io.zero88.jooqx.spi.mssql.MSSQLErrorConverterProvider;
import io.zero88.jooqx.spi.mssql.MSSQLPoolProvider;
import io.zero88.qwe.sql.handler.JooqxExtension;

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

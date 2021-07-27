package io.zero88.qwe.sql.spi.extension.pg;

import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxReactiveExtension;

/**
 * QWE Jooqx reactive extension for {@code PostgreSQL}
 *
 * @see PgPool
 * @see PgPoolProvider
 * @see PgSQLErrorConverterProvider
 * @see JooqxReactiveExtension
 */
public class PgJooqxReactiveExtension
    implements JooqxReactiveExtension<PgPool>, PgPoolProvider, PgSQLErrorConverterProvider {

    public static final PgJooqxReactiveExtension INSTANCE = new PgJooqxReactiveExtension();

}

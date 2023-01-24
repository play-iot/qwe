package io.zero88.qwe.sql.spi.extension.pg;

import io.vertx.pgclient.PgPool;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.zero88.qwe.sql.handler.JooqxExtension;

/**
 * QWE Jooqx reactive extension for {@code PostgreSQL}
 *
 * @see PgPool
 * @see PgPoolProvider
 * @see PgSQLErrorConverterProvider
 * @see JooqxExtension
 */
public class PgJooqxExtension
    implements JooqxExtension<PgPool>, PgPoolProvider, PgSQLErrorConverterProvider {

    public static final PgJooqxExtension INSTANCE = new PgJooqxExtension();

}

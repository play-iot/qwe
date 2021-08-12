package io.zero88.qwe.sql.h2;

import java.time.LocalDate;

import org.jooq.Catalog;

import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.JooqxBase;
import io.zero88.jooqx.JooqxPreparedQuery;
import io.zero88.jooqx.JooqxResultCollector;
import io.zero88.qwe.sql.handler.EntityHandlerFacade.JDBCEntityHandler;
import io.zero88.qwe.sql.handler.schema.SchemaInitializer;
import io.zero88.qwe.sql.integtest.h2.DefaultCatalog;
import io.zero88.qwe.sql.integtest.h2.Tables;
import io.zero88.qwe.sql.integtest.h2.tables.Author;

import lombok.NonNull;

class H2EntityHandler extends JDBCEntityHandler {

    @Override
    public @NonNull Catalog catalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public @NonNull Author table() {
        return Tables.AUTHOR;
    }

    @Override
    public @NonNull SchemaInitializer<JDBCPool, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector,
                                             JooqxBase<JDBCPool>> initializer() {
        return handler -> handler.jooqx()
                                 .execute(dsl -> dsl.insertInto(Tables.AUTHOR, Tables.AUTHOR.FIRST_NAME,
                                                                Tables.AUTHOR.LAST_NAME, Tables.AUTHOR.DATE_OF_BIRTH)
                                                    .values("zero88", "vn", LocalDate.of(1988, 6, 24))
                                                    .returning(), DSLAdapter.fetchOne(Tables.AUTHOR))
                                 .map(r -> new JsonObject().put("records", r));
    }

}

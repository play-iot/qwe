package cloud.playio.qwe.sql.h2;

import java.time.LocalDate;

import org.jooq.Catalog;

import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Tuple;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqxBase;
import io.github.zero88.jooqx.JooqxPreparedQuery;
import io.github.zero88.jooqx.JooqxResultCollector;
import cloud.playio.qwe.sql.handler.EntityHandlerFacade.JDBCEntityHandler;
import cloud.playio.qwe.sql.handler.schema.SchemaInitializer;
import cloud.playio.qwe.sql.integtest.h2.DefaultCatalog;
import cloud.playio.qwe.sql.integtest.h2.Tables;
import cloud.playio.qwe.sql.integtest.h2.tables.Author;

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
    public @NonNull SchemaInitializer<JDBCPool, Tuple, JooqxPreparedQuery, JooqxResultCollector,
                                             JooqxBase<JDBCPool>> initializer() {
        return handler -> handler.jooqx()
                                 .execute(dsl -> dsl.insertInto(Tables.AUTHOR, Tables.AUTHOR.FIRST_NAME,
                                                                Tables.AUTHOR.LAST_NAME, Tables.AUTHOR.DATE_OF_BIRTH)
                                                    .values("zero88", "vn", LocalDate.of(1988, 6, 24))
                                                    .returning(), DSLAdapter.fetchOne(Tables.AUTHOR))
                                 .map(r -> new JsonObject().put("records", r));
    }

}

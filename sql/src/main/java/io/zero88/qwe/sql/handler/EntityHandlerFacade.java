package io.zero88.qwe.sql.handler;

import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.JooqxBase;
import io.zero88.jooqx.JooqxPreparedQuery;
import io.zero88.jooqx.JooqxResultCollector;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.qwe.sql.handler.schema.SchemaHandler;

import lombok.NonNull;

public interface EntityHandlerFacade<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                        E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends EntityHandler<S, B, PQ, RS, RC, E>, SchemaHandler<S, B, PQ, RS, RC, E> {

    @Override
    @NonNull
    default SchemaHandler<S, B, PQ, RS, RC, E> schemaHandler() {
        return this;
    }

    // @formatter:off
    abstract class ReactiveEntityHandler<S extends Pool> extends
        EntityHandlerImpl<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxBase<S>>
        implements EntityHandlerFacade<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxBase<S>> {
    // @formatter:on
    }


    abstract class JDBCEntityHandler extends ReactiveEntityHandler<JDBCPool> {

    }

}

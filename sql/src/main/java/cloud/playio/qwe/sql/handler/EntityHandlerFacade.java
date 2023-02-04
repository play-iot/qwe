package cloud.playio.qwe.sql.handler;

import io.github.zero88.jooqx.JooqxBase;
import io.github.zero88.jooqx.JooqxPreparedQuery;
import io.github.zero88.jooqx.JooqxResultCollector;
import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
import cloud.playio.qwe.sql.handler.schema.SchemaHandler;

import lombok.NonNull;

public interface EntityHandlerFacade<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                        E extends SQLExecutor<S, B, PQ, RC>>
    extends EntityHandler<S, B, PQ, RC, E>, SchemaHandler<S, B, PQ, RC, E> {

    @Override
    @NonNull
    default SchemaHandler<S, B, PQ, RC, E> schemaHandler() {
        return this;
    }

    abstract class ReactiveEntityHandler<S extends Pool>
        extends EntityHandlerImpl<S, Tuple, JooqxPreparedQuery, JooqxResultCollector, JooqxBase<S>>
        implements EntityHandlerFacade<S, Tuple, JooqxPreparedQuery, JooqxResultCollector, JooqxBase<S>> { }


    abstract class JDBCEntityHandler extends ReactiveEntityHandler<JDBCPool> {

    }

}

package io.zero88.qwe.sql.handler;

import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;

public interface HasEntityHandler<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                     E extends SQLExecutor<S, B, PQ, RS, RC>> {

    EntityHandler<S, B, PQ, RS, RC, E> entityHandler();

}

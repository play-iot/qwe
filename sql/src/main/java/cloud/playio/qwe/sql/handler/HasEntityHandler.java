package cloud.playio.qwe.sql.handler;

import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;

public interface HasEntityHandler<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                     E extends SQLExecutor<S, B, PQ, RC>> {

    EntityHandler<S, B, PQ, RC, E> entityHandler();

}

package cloud.playio.qwe.sql.handler.schema;

import io.vertx.core.Future;
import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.sql.SQLLogSystem;
import cloud.playio.qwe.sql.handler.EntityHandler;

import lombok.NonNull;

/**
 * Represents for schema executor.
 *
 * @since 1.0.0
 */
public interface SchemaExecutor<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                   E extends SQLExecutor<S, B, PQ, RC>>
    extends SQLLogSystem {

    /**
     * Execute task.
     *
     * @param entityHandler entity handler
     * @return the result in single
     * @see EntityHandler
     * @since 1.0.0
     */
    Future<EventMessage> execute(@NonNull EntityHandler<S, B, PQ, RC, E> entityHandler);

}

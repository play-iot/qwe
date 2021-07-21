package io.zero88.qwe.sql.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;

/**
 * Represents for Schema migrator.
 *
 * @since 1.0.0
 */
public interface SchemaMigrator<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                   E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends SchemaExecutor<S, B, PQ, RS, RC, E> {

    SchemaMigrator NON_MIGRATOR = entityHandler -> Future.succeededFuture(
        EventMessage.success(EventAction.MIGRATE, new JsonObject().put("records", "No migrate")));

}

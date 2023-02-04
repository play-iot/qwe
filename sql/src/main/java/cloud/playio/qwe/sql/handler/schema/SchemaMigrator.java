package cloud.playio.qwe.sql.handler.schema;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.sql.SQLError.SQLMigrationError;
import cloud.playio.qwe.sql.handler.EntityHandler;
import lombok.NonNull;

/**
 * Represents for Schema migrator.
 *
 * @since 1.0.0
 */
public interface SchemaMigrator<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                   E extends SQLExecutor<S, B, PQ, RC>>
    extends SchemaExecutor<S, B, PQ, RC, E> {

    SchemaMigrator NON_MIGRATOR = entityHandler -> Future.succeededFuture(new JsonObject().put("migration", "Nope"));

    @Override
    default Logger logger() {
        return LogManager.getLogger(SchemaMigrator.class);
    }

    @Override
    default Future<EventMessage> execute(@NonNull EntityHandler<S, B, PQ, RC, E> entityHandler) {
        return doMigrate(entityHandler).recover(t -> Future.failedFuture(new SQLMigrationError(t)))
                                       .map(b -> EventMessage.success(EventAction.MIGRATE, b));
    }

    /**
     * Init default data
     *
     * @param handler an entity handler
     * @return json result in future
     */
    @NonNull Future<JsonObject> doMigrate(@NonNull EntityHandler<S, B, PQ, RC, E> handler);

}


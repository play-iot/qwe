package io.zero88.qwe.sql;

import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.Future;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginVerticle;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.exceptions.InitializerError.MigrationError;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.sql.handler.EntityHandler;
import io.zero88.qwe.sql.handler.JooqxExtension;

import lombok.NonNull;

public final class SQLPlugin<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends PluginVerticle<SQLPluginConfig, SQLPluginContext<EntityHandler<S, B, PQ, RS, RC, E>>> {

    private final EntityHandler<S, B, PQ, RS, RC, E> handler;
    private final Class<JooqxExtension<S, B, PQ, RS, RC, E>> jooqxExtensionClass;

    SQLPlugin(SharedDataLocalProxy sharedData, Class<EntityHandler<S, B, PQ, RS, RC, E>> handlerClass,
              Class<JooqxExtension<S, B, PQ, RS, RC, E>> jooqxExtensionClass) {
        super(sharedData);
        this.handler = Objects.requireNonNull(ReflectionClass.createObject(handlerClass),
                                              "Not found Entity Handler[" + handlerClass.getName() + "]");
        this.jooqxExtensionClass = jooqxExtensionClass;
    }

    @Override
    public Class<SQLPluginConfig> configClass() {
        return SQLPluginConfig.class;
    }

    @Override
    public String configFile() {
        return "sql.json";
    }

    @Override
    public String pluginName() {
        return "sql-plugin";
    }

    @Override
    public Future<Void> onAsyncStart() {
        return handler.setup(sharedData(), jooqxExtensionClass, pluginConfig().validate(), pluginContext())
                      .flatMap(this::initOrMigrate)
                      .mapEmpty();
    }

    @Override
    public SQLPluginContext<EntityHandler<S, B, PQ, RS, RC, E>> enrichContext(@NonNull PluginContext pluginContext,
                                                                              boolean isPostStep) {
        return new SQLPluginContext<>(pluginContext, handler);
    }

    private Future<EventMessage> initOrMigrate(EntityHandler<S, B, PQ, RS, RC, E> h) {
        return h.schemaHandler()
                .execute(h)
                .recover(t -> Future.failedFuture(new InitializerError("Unknown error when setting up database", t)))
                .flatMap(this::validateInitOrMigrationData)
                .onSuccess(r -> {
                    logger().info("{} SQL plugin [{}]", r.getAction(), r.rawData());
                    logger().info("DATABASE IS READY TO USE");
                });
    }

    private Future<EventMessage> validateInitOrMigrationData(EventMessage result) {
        if (!result.isError()) {
            return Future.succeededFuture(result);
        }
        ErrorMessage error = result.getError();
        Throwable t = error.getThrowable();
        if (Objects.isNull(t)) {
            t = new QWEException(error.getCode(), error.getMessage());
        }
        if (result.getAction() == EventAction.INIT) {
            return Future.failedFuture(new InitializerError("Failed to startup SQL component", t));
        } else {
            return Future.failedFuture(new MigrationError("Failed to startup SQL component", t));
        }
    }

}

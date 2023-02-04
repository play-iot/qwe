package cloud.playio.qwe.sql;

import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.Future;
import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import cloud.playio.qwe.PluginContext;
import cloud.playio.qwe.PluginVerticle;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.handler.JooqxBaseExtension;

import lombok.NonNull;

public final class SQLPlugin<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                E extends SQLExecutor<S, B, PQ, RC>>
    extends PluginVerticle<SQLPluginConfig, SQLPluginContext<EntityHandler<S, B, PQ, RC, E>>> {

    private final EntityHandler<S, B, PQ, RC, E> handler;
    private final Class<JooqxBaseExtension<S, B, PQ, RC, E>> jooqxExtensionClass;

    SQLPlugin(Class<EntityHandler<S, B, PQ, RC, E>> handlerClass,
              Class<JooqxBaseExtension<S, B, PQ, RC, E>> jooqxExtensionClass) {
        this.handler             = Objects.requireNonNull(ReflectionClass.createObject(handlerClass),
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
    public String configKey() {
        return SQLPluginConfig.KEY;
    }

    @Override
    public String pluginName() {
        return "sql-plugin";
    }

    @Override
    public Future<Void> onAsyncStart() {
        return handler.setup(sharedData(), pluginContext(), pluginConfig().validate(), jooqxExtensionClass)
                      .flatMap(this::initOrMigrate)
                      .mapEmpty();
    }

    @Override
    public SQLPluginContext<EntityHandler<S, B, PQ, RC, E>> enrichContext(@NonNull PluginContext pluginContext,
                                                                          boolean isPostStep) {
        return new SQLPluginContext<>(pluginContext, handler);
    }

    private Future<EventMessage> initOrMigrate(EntityHandler<S, B, PQ, RC, E> h) {
        return h.schemaHandler()
                .execute(h)
                .flatMap(msg -> !msg.isError()
                                ? Future.succeededFuture(msg)
                                : Future.failedFuture(msg.getError().getThrowable()))
                .onSuccess(r -> {
                    logger().info("{} SQL plugin [{}]", r.getAction(), r.rawData());
                    logger().info("DATABASE IS READY TO USE");
                });
    }

}

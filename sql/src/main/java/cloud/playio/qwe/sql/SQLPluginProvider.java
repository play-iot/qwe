package cloud.playio.qwe.sql;

import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import cloud.playio.qwe.PluginProvider;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.handler.JooqxBaseExtension;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@SuppressWarnings("rawtypes")
public final class SQLPluginProvider<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                        E extends SQLExecutor<S, B, PQ, RC>>
    implements PluginProvider<SQLPlugin> {

    private final Class<EntityHandler<S, B, PQ, RC, E>> entityHandlerClass;
    private Class<JooqxBaseExtension<S, B, PQ, RC, E>> jooqxExtensionClass;

    @Override
    public Class<SQLPlugin> pluginClass() {
        return SQLPlugin.class;
    }

    @Override
    public SQLPlugin get() {
        return new SQLPlugin<>(entityHandlerClass, jooqxExtensionClass);
    }

}

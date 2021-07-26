package io.zero88.qwe.sql.spi.extension.jdbc;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.provider.DBEmbeddedProvider;
import io.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.zero88.jooqx.spi.jdbc.JDBCPoolProvider;
import io.zero88.qwe.PluginContext;
import io.zero88.qwe.sql.SQLPluginConfig;
import io.zero88.qwe.sql.handler.JooqxExtension.JooqxReactiveExtension;

/**
 * QWE Jooqx extension that wraps JDBC pool
 */
public class JDBCPoolJooqxExtension<P extends DataSourceProvider>
    implements JooqxReactiveExtension<JDBCPool>, JDBCPoolProvider<P>, JDBCErrorConverterProvider {

    private final JDBCPoolProvider<P> clientProvider;

    public JDBCPoolJooqxExtension(JDBCPoolProvider<P> clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public @NotNull JDBCPoolJooqxExtension<P> setup(@NotNull PluginContext pluginContext,
                                                    @NotNull SQLPluginConfig pluginConfig) {
        DBEmbeddedProvider embedded = DBEmbeddedDelegate.create(pluginContext.appName(), pluginConfig.getDialect(),
                                                                pluginConfig.getEmbeddedMode());
        if (Objects.nonNull(embedded)) {
            pluginConfig.connectionOptions(
                embedded.createConnOptions(embedded.init(), pluginConfig.connectionOptions()));
        }
        return (JDBCPoolJooqxExtension<P>) JooqxReactiveExtension.super.setup(pluginContext, pluginConfig);
    }

    @Override
    public @NotNull JDBCPoolProvider<P> clientProvider() {
        return clientProvider;
    }

    @Override
    public Class<P> dataSourceProviderClass() {
        return clientProvider.dataSourceProviderClass();
    }

    @Override
    public String jdbcDataSourceClass() {
        return clientProvider.jdbcDataSourceClass();
    }

}

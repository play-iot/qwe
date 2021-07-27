package io.zero88.qwe.sql.spi.extension.jdbc;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.utils.FileUtils;
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
        Path dbPath = Optional.ofNullable(pluginContext.dataDir())
                              .map(p -> p.resolve(pluginContext.appName()))
                              .orElseGet(() -> FileUtils.defaultDatadir(pluginContext.appName()))
                              .resolve(Optional.ofNullable(pluginConfig.getPluginDir()).orElse(""));
        DBEmbeddedProvider embedded = DBEmbeddedDelegate.create(dbPath.toAbsolutePath().toString(),
                                                                pluginConfig.getDialect(),
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

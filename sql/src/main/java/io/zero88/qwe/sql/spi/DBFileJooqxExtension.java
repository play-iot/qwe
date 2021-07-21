package io.zero88.qwe.sql.spi;

import java.nio.file.Paths;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.utils.Strings;
import io.github.zero88.utils.UUID64;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.provider.DBFileJooqxFacade;
import io.zero88.qwe.PluginContext;
import io.zero88.qwe.sql.handler.JooqxExtension;

import lombok.NonNull;

/**
 * QWE Jooqx extension for Database in local file
 *
 * @see DBFileJooqxFacade
 */
// @formatter:off
public abstract class DBFileJooqxExtension<S, B, PQ extends SQLPreparedQuery<B>, RS,
                                              RC extends SQLResultCollector<RS>,
                                              E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends DBFileJooqxFacade<S, B, PQ, RS, RC, E> implements JooqxExtension<S, B, PQ, RS, RC, E> {
// @formatter:on

    private PluginContext pluginContext;

    @Override
    public @NotNull JooqxExtension<S, B, PQ, RS, RC, E> setup(@NotNull PluginContext pluginContext) {
        return JooqxExtension.super.setup(this.pluginContext = pluginContext);
    }

    @Override
    public @NotNull String init() {
        return pluginContext.appName();
    }

    @Override
    public @NonNull JsonObject createConnOptions(String databaseName) {
        String dbPath = Paths.get(databaseName).isAbsolute()
                        ? databaseName
                        : Objects.requireNonNull(pluginContext.dataDir()).resolve(databaseName).toString();
        return new JsonObject().put("jdbcUrl", protocol() + dbPath).put("driverClassName", driverClassName());
    }

    @Override
    public String user(JsonObject connOptions) {
        String user = connOptions.getString("username", connOptions.getString("user"));
        if (Strings.isBlank(user)) {
            user = "admin";
        }
        logger().info("Database user [{}]", user);
        return user;
    }

    @Override
    public String password(JsonObject connOptions) {
        String password = connOptions.getString("password");
        if (Strings.isBlank(password)) {
            password = UUID64.random();
            logger().info("Database password [{}]", password);
        }
        return password;
    }

}

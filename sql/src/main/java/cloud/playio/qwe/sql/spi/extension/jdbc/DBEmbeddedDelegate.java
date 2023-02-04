package cloud.playio.qwe.sql.spi.extension.jdbc;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;

import io.github.zero88.jooqx.provider.DBEmbeddedProvider;
import io.github.zero88.jooqx.spi.DBEmbeddedMode;
import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.exceptions.InitializerError;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DBEmbeddedDelegate<T extends DBEmbeddedProvider> implements DBEmbeddedProvider {

    private final T provider;

    @Override
    public @NotNull String init() {
        return provider.init();
    }

    @Override
    public @NotNull String protocol() {
        return provider.protocol();
    }

    @Override
    public @NonNull String driverClassName() {
        return provider.driverClassName();
    }

    @Override
    public @NotNull JsonObject createConnOptions(@NotNull String databaseName, @NotNull JsonObject connOptions) {
        JsonObject conn = DBEmbeddedProvider.super.createConnOptions(databaseName, connOptions);
        String driverClassName = conn.getString("driverClassName", conn.getString("driver_class"));
        if (Objects.nonNull(driverClassName) && !ReflectionClass.hasClass(driverClassName)) {
            throw new InitializerError("Unable load SQL driver [" + driverClassName + "]");
        }
        return conn;
    }

    public static DBEmbeddedProvider create(String databaseName, SQLDialect dialect, DBEmbeddedMode mode) {
        if (DBEmbeddedMode.isSupportFileMode(dialect) && DBEmbeddedMode.isFile(mode)) {
            return new DBFileDelegate(databaseName, dialect);
        }
        if (DBEmbeddedMode.isSupportMemoryMode(dialect) && DBEmbeddedMode.isMemory(mode)) {
            return new DBMemoryDelegate(dialect);
        }
        return null;
    }

}

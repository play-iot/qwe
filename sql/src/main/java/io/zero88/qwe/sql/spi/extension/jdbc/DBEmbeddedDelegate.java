package io.zero88.qwe.sql.spi.extension.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;

import io.zero88.jooqx.provider.DBEmbeddedProvider;
import io.zero88.jooqx.spi.DBEmbeddedMode;

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

package io.zero88.qwe.sql;

import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginContext.DefaultPluginContext;
import io.zero88.qwe.sql.handler.EntityHandler;
import io.zero88.qwe.sql.handler.HasEntityHandler;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public final class SQLPluginContext<T extends EntityHandler> extends DefaultPluginContext implements HasEntityHandler {

    @Getter
    @Accessors(fluent = true)
    private final T entityHandler;

    SQLPluginContext(@NonNull PluginContext ctx, T entityHandler) {
        super(ctx);
        this.entityHandler = entityHandler;
    }

}

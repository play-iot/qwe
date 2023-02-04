package cloud.playio.qwe.sql;

import cloud.playio.qwe.PluginContext;
import cloud.playio.qwe.PluginContext.DefaultPluginContext;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.handler.HasEntityHandler;

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

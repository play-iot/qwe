package io.zero88.qwe.sql.pg;

import org.jooq.Catalog;
import org.jooq.Table;

import io.vertx.pgclient.PgPool;
import io.zero88.qwe.sql.handler.EntityHandlerFacade.ReactiveEntityHandler;
import io.zero88.qwe.sql.integtest.h2.DefaultCatalog;
import io.zero88.qwe.sql.integtest.h2.Tables;

import lombok.NonNull;

public class PgEntityHandler extends ReactiveEntityHandler<PgPool> {

    @Override
    public @NonNull Catalog catalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public @NonNull Table table() {
        return Tables.AUTHOR;
    }

}

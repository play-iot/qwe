package io.zero88.qwe.sql.pg;

import io.zero88.qwe.sql.SQLPluginProvider;
import io.zero88.qwe.sql.SQLPluginTest;

public abstract class PgTestBase extends SQLPluginTest<PgEntityHandler> {

    @Override
    public SQLPluginProvider initProvider() {
        return new SQLPluginProvider(PgEntityHandler.class);
    }

}

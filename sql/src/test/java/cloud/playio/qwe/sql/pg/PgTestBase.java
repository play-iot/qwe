package cloud.playio.qwe.sql.pg;

import cloud.playio.qwe.sql.SQLPluginProvider;
import cloud.playio.qwe.sql.SQLPluginTest;

public abstract class PgTestBase extends SQLPluginTest<PgEntityHandler> {

    @Override
    public SQLPluginProvider initProvider() {
        return new SQLPluginProvider(PgEntityHandler.class);
    }

}

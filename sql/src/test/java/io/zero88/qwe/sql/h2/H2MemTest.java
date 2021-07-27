package io.zero88.qwe.sql.h2;

import org.jooq.SQLDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.spi.DBEmbeddedMode;
import io.zero88.qwe.PluginProvider;
import io.zero88.qwe.sql.SQLPlugin;
import io.zero88.qwe.sql.SQLPluginConfig;
import io.zero88.qwe.sql.SQLPluginProvider;
import io.zero88.qwe.sql.SQLPluginTest;
import io.zero88.qwe.sql.integtest.h2.Tables;

public class H2MemTest extends SQLPluginTest<H2EntityHandler> {

    @Override
    public SQLPluginConfig initConfig() {
        return new SQLPluginConfig().setEmbeddedMode(DBEmbeddedMode.MEMORY)
                                    .setDialect(SQLDialect.H2)
                                    .setAutoDetect(true)
                                    .connectionOptions(new JsonObject().put("maximumPoolSize", 3));
    }

    @Override
    public PluginProvider<SQLPlugin> initProvider() {
        return new SQLPluginProvider(H2EntityHandler.class);
    }

    @Test
    public void simpleAssertInitData(VertxTestContext testContext) {
        Checkpoint cp = testContext.checkpoint();
        entityHandler.jooqx()
                     .execute(dsl -> dsl.selectFrom(Tables.AUTHOR), DSLAdapter.fetchMany(Tables.AUTHOR))
                     .map(records -> testContext.verify(() -> {
                         Assertions.assertEquals(1, records.size());
                         cp.flag();
                     }));
    }

}

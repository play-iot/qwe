package io.zero88.qwe.sql.h2;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.qwe.PluginProvider;
import io.zero88.qwe.sql.SQLPlugin;
import io.zero88.qwe.sql.SQLPluginProvider;
import io.zero88.qwe.sql.SQLPluginTest;
import io.zero88.qwe.sql.integtest.h2.Tables;

public abstract class H2TestBase extends SQLPluginTest<H2EntityHandler> {

    @Override
    public PluginProvider<SQLPlugin> initProvider() {
        return new SQLPluginProvider(H2EntityHandler.class);
    }

    @Test
    public void simpleAssertInitData(VertxTestContext testContext) {
        Checkpoint cp = testContext.checkpoint();
        entityHandler.jooqx()
                     .execute(dsl -> dsl.selectFrom(Tables.AUTHOR), DSLAdapter.fetchMany(Tables.AUTHOR))
                     .map(recs -> testContext.verify(() -> {
                         Assertions.assertEquals(1, recs.size());
                         Assertions.assertEquals("zero88", recs.get(0).getFirstName());
                         Assertions.assertEquals("vn", recs.get(0).getLastName());
                         Assertions.assertEquals(LocalDate.of(1988, 6, 24), recs.get(0).getDateOfBirth());
                         cp.flag();
                     }));
    }

}

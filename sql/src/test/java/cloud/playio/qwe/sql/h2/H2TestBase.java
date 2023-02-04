package cloud.playio.qwe.sql.h2;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.DSLAdapter;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

import cloud.playio.qwe.PluginProvider;
import cloud.playio.qwe.sql.SQLPlugin;
import cloud.playio.qwe.sql.SQLPluginProvider;
import cloud.playio.qwe.sql.SQLPluginTest;
import cloud.playio.qwe.sql.integtest.h2.Tables;

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
                     .onSuccess(recs -> testContext.verify(() -> {
                         Assertions.assertEquals(1, recs.size());
                         Assertions.assertEquals("zero88", recs.get(0).getFirstName());
                         Assertions.assertEquals("vn", recs.get(0).getLastName());
                         Assertions.assertEquals(LocalDate.of(1988, 6, 24), recs.get(0).getDateOfBirth());
                         cp.flag();
                     }))
                     .onFailure(testContext::failNow);
    }

}

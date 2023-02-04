package cloud.playio.qwe.sql.pg;

import org.jooq.SQLDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

import cloud.playio.qwe.PluginDeploymentHelper;
import cloud.playio.qwe.sql.SQLPluginConfig;
import cloud.playio.qwe.sql.SQLPluginFailedTest;
import cloud.playio.qwe.sql.SQLPluginProvider;

class PgUnableConnectTest extends SQLPluginFailedTest {

    @Test
    void unable_connect(Vertx vertx, VertxTestContext testContext) {
        Checkpoint cp = testContext.checkpoint();
        SQLPluginConfig config = new SQLPluginConfig().setDialect(SQLDialect.POSTGRES)
                                                      .setAutoDetect(true)
                                                      .connectionOptions(new JsonObject().put("port", 5433));
        PluginDeploymentHelper.Junit5.create(this)
                                     .deployFailed(vertx, testContext, config,
                                                   new SQLPluginProvider(PgEntityHandler.class),
                                                   t -> testContext.verify(() -> {
                                                       t.printStackTrace();
                                                       Assertions.assertTrue(t instanceof RuntimeException);
                                                       Assertions.assertNotNull(t.getCause());
                                                       Assertions.assertEquals(
                                                           "Connection refused: localhost/127.0.0.1:5433",
                                                           t.getCause().getMessage());
                                                       cp.flag();
                                                   }));
    }

}

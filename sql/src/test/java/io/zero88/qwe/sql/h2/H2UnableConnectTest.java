package io.zero88.qwe.sql.h2;

import org.jooq.SQLDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.qwe.sql.SQLPluginConfig;
import io.zero88.qwe.sql.SQLPluginFailedTest;
import io.zero88.qwe.sql.SQLPluginProvider;
import io.zero88.qwe.sql.spi.extension.jdbc.JDBCPoolHikariJooqxExtension;

import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

public class H2UnableConnectTest extends SQLPluginFailedTest {

    @Test
    public void unable_connect(Vertx vertx, VertxTestContext testContext) {
        Checkpoint cp = testContext.checkpoint();
        JsonObject connOptions = new JsonObject().put("jdbcUrl", "jdbc:h2:tcp://localhost:9092/unknown")
                                                 .put("driverClassName", "org.h2.Driver");
        SQLPluginConfig config = new SQLPluginConfig().setDialect(SQLDialect.H2).connectionOptions(connOptions);
        deployFailed(vertx, testContext, config,
                     new SQLPluginProvider(H2EntityHandler.class, JDBCPoolHikariJooqxExtension.class),
                     t -> testContext.verify(() -> {
                         Assertions.assertTrue(t instanceof PoolInitializationException);
                         cp.flag();
                     }));
    }

}

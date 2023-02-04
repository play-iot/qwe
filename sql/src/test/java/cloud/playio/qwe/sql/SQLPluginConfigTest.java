package cloud.playio.qwe.sql;

import org.jooq.SQLDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.spi.DBEmbeddedMode;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import cloud.playio.qwe.IConfig;
import cloud.playio.qwe.JsonHelper;
import cloud.playio.qwe.sql.spi.extension.jdbc.JDBCPoolHikariJooqxExtension;
import cloud.playio.qwe.utils.JsonUtils;

class SQLPluginConfigTest {

    @Test
    void test_serialize_deserialize_default() {
        final SQLPluginConfig config = IConfig.fromClasspath("sql.json", SQLPluginConfig.class);
        Assertions.assertNotNull(config);
        Assertions.assertEquals("sql", config.getPluginDir());
        Assertions.assertNull(config.getDialect());
        Assertions.assertNull(config.getJooqxExtensionClass());
        Assertions.assertFalse(config.isAutoDetect());
        Assertions.assertEquals(new JsonObject(), config.connectionOptions());
        Assertions.assertEquals(new JsonObject(), config.poolOptions());
        Assertions.assertThrows(NullPointerException.class, config::validate);
        JsonHelper.assertJson(
            new JsonObject("{\"__sql_conn__\":{},\"__sql_pool__\":{},\"pluginDir\":\"sql\"," + "\"autoDetect\":false}"),
            config.toJson());
    }

    @Test
    void test_serialize_deserialize_custom() {
        SQLPluginConfig c = new SQLPluginConfig().setPluginDir("h2")
                                                 .setDialect(SQLDialect.H2)
                                                 .setJooqxExtensionClass(JDBCPoolHikariJooqxExtension.class.getName())
                                                 .connectionOptions(new JDBCConnectOptions().setUser("xxx").toJson())
                                                 .poolOptions(new PoolOptions().setMaxSize(3).toJson());
        Assertions.assertNotNull(c);
        Assertions.assertNotNull(c.validate());
        Assertions.assertEquals("h2", c.getPluginDir());
        Assertions.assertEquals(SQLDialect.H2, c.getDialect());
        Assertions.assertFalse(c.isAutoDetect());
        Assertions.assertEquals(JDBCPoolHikariJooqxExtension.class.getName(), c.getJooqxExtensionClass());
        Assertions.assertEquals(new JsonObject("{\"connectTimeout\":60000,\"dataSourceImplementation\":\"AGROAL\"," +
                                               "\"idleTimeout\":0,\"metricsEnabled\":false," +
                                               "\"tracingPolicy\":\"PROPAGATE\",\"user\":\"xxx\"}"),
                                c.connectionOptions());
        Assertions.assertEquals(new JsonObject("{\"connectionTimeout\":30,\"connectionTimeoutUnit\":\"SECONDS\"," +
                                               "\"idleTimeout\":0,\"idleTimeoutUnit\":\"SECONDS\",\"maxSize\":3," +
                                               "\"maxWaitQueueSize\":-1,\"poolCleanerPeriod\":1000}"), c.poolOptions());
        JsonHelper.assertJson(new JsonObject("{\"__sql_conn__\":{\"connectTimeout\":60000," +
                                             "\"dataSourceImplementation\":\"AGROAL\",\"idleTimeout\":0," +
                                             "\"metricsEnabled\":false,\"tracingPolicy\":\"PROPAGATE\"," +
                                             "\"user\":\"xxx\"},\"__sql_pool__\":{\"connectionTimeout\":30," +
                                             "\"connectionTimeoutUnit\":\"SECONDS\",\"idleTimeout\":0," +
                                             "\"idleTimeoutUnit\":\"SECONDS\",\"maxSize\":3,\"maxWaitQueueSize\":-1," +
                                             "\"poolCleanerPeriod\":1000},\"pluginDir\":\"h2\",\"dialect\":\"H2\"," +
                                             "\"jooqxExtensionClass\":\"cloud.playio.qwe.sql.spi.extension.jdbc" +
                                             ".JDBCPoolHikariJooqxExtension\",\"autoDetect\":false}"), c.toJson());
    }

    @Test
    void test_merge() {
        SQLPluginConfig c = new SQLPluginConfig().setDialect(SQLDialect.POSTGRES)
                                                 .setAutoDetect(true)
                                                 .setEmbeddedMode(DBEmbeddedMode.MEMORY);
        final SQLPluginConfig merged = IConfig.merge(JsonUtils.loadJsonInClasspath("sql.json"), c,
                                                     SQLPluginConfig.class);
        Assertions.assertNotNull(merged);
        Assertions.assertNotNull(merged.validate());
        Assertions.assertEquals("sql", merged.getPluginDir());
        Assertions.assertEquals(SQLDialect.POSTGRES, merged.getDialect());
        Assertions.assertTrue(merged.isAutoDetect());
        Assertions.assertNull(merged.getJooqxExtensionClass());
        Assertions.assertEquals(new JsonObject(), merged.connectionOptions());
        Assertions.assertEquals(new JsonObject(), merged.poolOptions());
        JsonHelper.assertJson(new JsonObject("{\"__sql_conn__\":{},\"__sql_pool__\":{},\"pluginDir\":\"sql\"," +
                                             "\"dialect\":\"POSTGRES\",\"autoDetect\":true," +
                                             "\"embeddedMode\":\"MEMORY\"}"), merged.toJson());
    }

}

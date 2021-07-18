package io.zero88.qwe;

import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.cluster.ClusterType;

class QWEBootConfigTest {

    @Test
    public void test_default() {
        QWEBootConfig cfg = new QWEBootConfig();
        System.out.println(cfg.toJson().encodePrettily());
        Assertions.assertEquals(2468, cfg.getEventBusOptions().getPort());
        Assertions.assertEquals(2468, cfg.getEventBusOptions().getClusterPublicPort());
        Assertions.assertFalse(cfg.getEventBusOptions().isReusePort());

        Assertions.assertEquals(Paths.get(System.getProperty("java.io.tmpdir", "."), "qwe-cache").toString(),
                                cfg.getFileSystemOptions().getFileCacheDir());

        Assertions.assertFalse(cfg.isHAEnabled());
        Assertions.assertEquals("__QWE__", cfg.getHAGroup());

        Assertions.assertEquals(ClusterType.NONE, cfg.getClusterType());
        Assertions.assertNull(cfg.getClusterConfigFile());
        Assertions.assertFalse(cfg.isClusterLiteMember());
    }

    @Test
    public void test_deserialize_default() {
        final QWEBootConfig qweBootConfig = IConfig.fromClasspath("def-system-cfg.json", QWEBootConfig.class);
        JsonHelper.assertJson(new QWEBootConfig().toJson(), qweBootConfig.toJson());
    }

    @Test
    public void test_deserialize_custom() {
        String jsonStr = "{\"__system__\":{\"clusterType\":\"HAZELCAST\",\"haGroup\":\"_PLAYIO_\",\"haEnabled\":true," +
                         "\"eventBusOptions\":{\"port\":7000,\"clusterPublicPort\":3333,\"reusePort\":false," +
                         "\"keyStoreOptions\":{\"password\":\"123\",\"path\":\"/tmp/key\"}}, " +
                         "\"fileSystemOptions\":{\"fileCacheDir\":\"/data/qwe-cache\"}}}";
        QWEBootConfig cfg = IConfig.from(jsonStr, QWEBootConfig.class);
        Assertions.assertNotNull(cfg);
        System.out.println(cfg.toJson().encodePrettily());

        Assertions.assertEquals(ClusterType.HAZELCAST, cfg.getClusterType());
        Assertions.assertEquals("_PLAYIO_", cfg.getHAGroup());
        Assertions.assertTrue(cfg.isHAEnabled());

        Assertions.assertFalse(cfg.getEventBusOptions().isReusePort());
        Assertions.assertEquals(7000, cfg.getEventBusOptions().getPort());
        Assertions.assertEquals(3333, cfg.getEventBusOptions().getClusterPublicPort());
        Assertions.assertEquals("/data/qwe-cache", cfg.getFileSystemOptions().getFileCacheDir());
        Assertions.assertEquals(new JsonObject("{\"password\":\"123\",\"path\":\"/tmp/key\"}"),
                                cfg.getEventBusOptions().getKeyStoreOptions().toJson());
    }

    @Test
    public void test_merged() {
        final QWEBootConfig def = new QWEBootConfig();
        final JsonObject override = new JsonObject(
            "{\"clusterType\":\"HAZELCAST\",\"haGroup\":\"_PLAYIO_\",\"clusterLiteMember\":true, " +
            "\"eventBusOptions\":{\"port\":7000, \"clusterPublicPort\":7001}," +
            "\"fileSystemOptions\":{\"fileCacheDir\":\"/data/qwe\"}}");
        final QWEBootConfig cfg = IConfig.merge(def, override, QWEBootConfig.class);
        System.out.println(cfg.toJson().encodePrettily());
        Assertions.assertEquals(7000, cfg.getEventBusOptions().getPort());
        Assertions.assertEquals(7001, cfg.getEventBusOptions().getClusterPublicPort());
        Assertions.assertFalse(cfg.getEventBusOptions().isReusePort());

        Assertions.assertEquals("/data/qwe", cfg.getFileSystemOptions().getFileCacheDir());

        Assertions.assertFalse(cfg.isHAEnabled());
        Assertions.assertEquals("_PLAYIO_", cfg.getHAGroup());

        Assertions.assertEquals(ClusterType.HAZELCAST, cfg.getClusterType());
        Assertions.assertTrue(cfg.isClusterLiteMember());
    }

}

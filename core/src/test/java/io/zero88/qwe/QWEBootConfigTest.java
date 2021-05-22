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
        System.out.println(cfg.toJson());
        Assertions.assertEquals(2468, cfg.getEventBusOptions().getPort());
        Assertions.assertEquals(2468, cfg.getEventBusOptions().getClusterPublicPort());
        Assertions.assertTrue(cfg.getEventBusOptions().isReusePort());

        Assertions.assertEquals(Paths.get(System.getProperty("java.io.tmpdir", "."), "qwe-cache").toString(),
                                cfg.getFileSystemOptions().getFileCacheDir());

        Assertions.assertFalse(cfg.isHAEnabled());
        Assertions.assertEquals("__QWE__", cfg.getHAGroup());

        Assertions.assertEquals(ClusterType.UNDEFINED, cfg.getClusterType());
    }

    @Test
    public void test_deserialize_custom() {
        String jsonStr = "{\"__system__\":{\"clusterType\":\"HAZELCAST\",\"haGroup\":\"_PLAYIO_\",\"haEnabled\":true," +
                         "\"eventBusOptions\":{\"port\":7000,\"reusePort\":false," +
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
        Assertions.assertEquals(7000, cfg.getEventBusOptions().getClusterPublicPort());
        Assertions.assertEquals("/data/qwe-cache", cfg.getFileSystemOptions().getFileCacheDir());
        Assertions.assertEquals(new JsonObject("{\"password\":\"123\",\"path\":\"/tmp/key\"}"),
                                cfg.getEventBusOptions().getKeyStoreOptions().toJson());
    }

}

package io.zero88.qwe;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.QWEConfig.QWEDeployConfig;
import io.zero88.qwe.exceptions.ConfigException;

public class QWEConfigTest {

    @Test
    public void test_default() {
        QWEConfig cfg = QWEConfig.create();
        QWEConfig from = IConfig.fromClasspath("system.json", QWEConfig.class);
        System.out.println(cfg.toJson().encodePrettily());
        Assertions.assertEquals(cfg.toJson(), from.toJson());

        Assertions.assertNull(from.getBootConfig());

        Assertions.assertNotNull(from.getDeployConfig());
        Assertions.assertEquals(new QWEDeployConfig().toJson(), from.getDeployConfig().toJson());

        Assertions.assertNotNull(from.getAppConfig());
        Assertions.assertEquals(QWEAppConfig.DEFAULT_DATADIR, from.getAppConfig().dataDir());
        Assertions.assertTrue(from.getAppConfig().other().isEmpty());
        Assertions.assertEquals(new DeliveryOptions().toJson(), from.getAppConfig().getDeliveryOptions().toJson());
    }

    @Test
    public void test_deserialize_from_blank_json() {
        Assertions.assertEquals(QWEConfig.create().toJson(), IConfig.from(new JsonObject(), QWEConfig.class).toJson());
    }

    @Test
    public void test_deserialize_full_system_config() throws JSONException {
        QWEConfig config = QWEConfig.builder().bootConfig(new QWEBootConfig()).build();
        System.out.println(config.getBootConfig().toJson().encodePrettily());
        QWEConfig from = IConfig.fromClasspath("full-system-cfg.json", QWEConfig.class);
        JsonHelper.assertJson(config.getBootConfig().toJson(), from.getBootConfig().toJson());
        Assertions.assertEquals("/data", from.getAppConfig().getDataDir());
        Assertions.assertEquals(config.getDeployConfig().toJson(), from.getDeployConfig().toJson());
        Assertions.assertEquals(config.getAppConfig().toJson(), from.getAppConfig().toJson());
    }

    @Test
    public void test_deserializeInvalidJson_shouldFailed() {
        TestHelper.assertThrows(() -> IConfig.from("hello", QWEConfig.class), ConfigException.class,
                                DecodeException.class);
    }

    @Test
    public void test_deserializeRootHaveRedundantProperties_shouldFailed() {
        String jsonStr = "{\"__redundant__\":{},\"__system__\":{}}";
        TestHelper.assertThrows(() -> IConfig.from(jsonStr, QWEConfig.class), ConfigException.class,
                                IllegalArgumentException.class);
    }

    //    @Test
    //    public void test_deserialize_plain_child() {
    //        String jsonStr = "{\"active\":false,\"ha\":false,\"name\":\"\"," +
    //                         "\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",
    //                            \"options\":{}}";
    //        SystemConfig.ClusterConfig cfg = IConfig.from(jsonStr, SystemConfig.ClusterConfig.class);
    //        Assertions.assertNotNull(cfg);
    //        Assertions.assertEquals(ClusterType.HAZELCAST, cfg.getType());
    //    }
    //
    //    @Test
    //    public void test_deserialize_child_from_parent_lvl1() {
    //        String jsonStr = "{\"__cluster__\":{\"active\":false,\"ha\":false,\"name\":\"\"," +
    //                         "\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",
    //                            \"options\":{}}}";
    //        SystemConfig.ClusterConfig cfg = IConfig.from(jsonStr, SystemConfig.ClusterConfig.class);
    //        Assertions.assertNotNull(cfg);
    //        Assertions.assertEquals(ClusterType.HAZELCAST, cfg.getType());
    //    }

    //    @Test
    //    public void test_deserialize_system_config() {
    //        String jsonStr = "{\"__cluster__\":{\"active\":false,\"ha\":false,\"name\":\"\",\"type\":\"HAZELCAST\"," +
    //                         "\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}," +
    //                         "\"__eventBus__\":{\"sendBufferSize\":-1,\"receiveBufferSize\":-1,\"trafficClass\":-1," +
    //                         "\"reuseAddress\":true,\"logActivity\":false,\"reusePort\":false,\"tcpNoDelay\":true," +
    //                         "\"tcpKeepAlive\":false,\"soLinger\":-1,\"usePooledBuffers\":false,\"idleTimeout\":0," +
    //                         "\"idleTimeoutUnit\":\"SECONDS\",\"ssl\":false,\"enabledCipherSuites\":[],
    //                            \
    //        "crlPaths\":[]," + "\"crlValues\":[],\"useAlpn\":false,\"enabledSecureTransportProtocols\":[\"TLSv1\",
    //                            \
    //        "TLSv1" + ".1\",\"TLSv1.2\"],\"tcpFastOpen\":false,\"tcpCork\":false,\"tcpQuickAck\":false," +
    //        "\"clustered\":false,\"clusterPublicPort\":0,\"clusterPingInterval\":20000," +
    //        "\"clusterPingReplyInterval\":20000,\"port\":0,\"host\":\"localhost\",
    //                            \
    //        "acceptBacklog\":-1," + "\"clientAuth\":\"NONE\",\"reconnectAttempts\":0,\"reconnectInterval\":1000," +
    //        "\"connectTimeout\":60000,\"trustAll\":true}}";
    //        SystemConfig cfg = IConfig.from(jsonStr, SystemConfig.class);
    //        Assertions.assertNotNull(cfg);
    //        Assertions.assertNotNull(cfg.getClusterConfig());
    //        Assertions.assertNotNull(cfg.getEventBusConfig());
    //    }

    //    @Test
    //    public void test_deserialize_child_from_root() {
    //        String jsonStr =
    //            "{\"dataDir\": \"\", \"__system__\":{\"__cluster__\":{\"active\":false,\"ha\":false,\"name\":\"\"," +
    //            "\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}}}";
    //        SystemConfig.ClusterConfig cfg = IConfig.from(jsonStr, SystemConfig.ClusterConfig.class);
    //        Assertions.assertNotNull(cfg);
    //        Assertions.assertEquals(ClusterType.HAZELCAST, cfg.getType());
    //    }

    //    @Test
    //    public void test_deserialize_appCfg_from_root() throws JSONException {
    //        String jsonStr = "{\"__system__\":{},\"__app__\":{\"http.port\":8085}}";
    //        QWEConfig cfg = IConfig.from(jsonStr, QWEConfig.class);
    //        Assertions.assertNotNull(cfg);
    //        Assertions.assertNotNull(cfg.getAppConfig());
    //        Assertions.assertEquals(8085, cfg.getAppConfig().lookup("http.port"));
    //        JSONAssert.assertEquals("{\"http.port\":8085}", cfg.getAppConfig().toJson().encode(), JSONCompareMode
    //       .STRICT);
    //        Assertions.assertNotNull(cfg.getSystemConfig());
    //        Assertions.assertNotNull(cfg.getSystemConfig().getClusterConfig());
    //        Assertions.assertNotNull(cfg.getSystemConfig().getEventBusConfig());
    //        Assertions.assertNotNull(cfg.getDeployConfig());
    //        Assertions.assertNotNull(cfg.getDataDir());
    //    }

    @Test
    public void test_blank() throws JSONException {
        QWEConfig cfg = QWEConfig.create();
        Assertions.assertNotNull(cfg);
        Assertions.assertNotNull(cfg.getAppConfig());
        Assertions.assertNotNull(cfg.getAppConfig().dataDir());
        Assertions.assertTrue(cfg.getAppConfig().other().isEmpty());
        Assertions.assertNotNull(cfg.getDeployConfig());
        JSONAssert.assertEquals("{\"worker\":false,\"workerPoolSize\":20," +
                                "\"maxWorkerExecuteTime\":60000000000,\"ha\":false,\"instances\":1," +
                                "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\"}",
                                cfg.getDeployConfig().toJson().encode(), JSONCompareMode.STRICT);
        Assertions.assertNull(cfg.getBootConfig());
    }

    @Test
    public void test_blank_with_app_cfg() throws JSONException {
        QWEConfig config = QWEConfig.create(new JsonObject().put("hello", 1));
        Assertions.assertNotNull(config);
        Assertions.assertNotNull(config.getAppConfig().dataDir());
        Assertions.assertNotNull(config.getAppConfig());
        Assertions.assertEquals(1, config.getAppConfig().other().size());
        Assertions.assertEquals(1, config.getAppConfig().lookup("hello"));
        Assertions.assertNotNull(config.getDeployConfig());
        JSONAssert.assertEquals("{\"worker\":false,\"workerPoolSize\":20," +
                                "\"maxWorkerExecuteTime\":60000000000,\"ha\":false,\"instances\":1," +
                                "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\"}",
                                config.getDeployConfig().toJson().encode(), JSONCompareMode.STRICT);
        Assertions.assertNull(config.getBootConfig());
    }

    //    @Test
    //    public void test_merge_with_default() throws JSONException {
    //        QWEConfig QWEConfig = IConfig.from(Configs.loadJsonConfig("system.json"), QWEConfig.class);
    //        String jsonInput = "{\"__system__\":{\"__eventBus__\":{\"clientAuth\":\"REQUIRED\",\"ssl\":true," +
    //                           "\"clustered\":true,\"keyStoreOptions\":{\"path\":\"eventBusKeystore.jks\"," +
    //                           "\"password\":\"qwesparkEventBus\"},
    //                          \"trustStoreOptions\":{\"path\":\"eventBusKeystore" +
    //                           ".jks\",\"password\":\"qwesparkEventBus\"}},\"__cluster__\":{\"active\":true,
    //                          \"ha\":true," +
    //                           "\"listenerAddress\":\"io.zero88.dashboard.connector.edge.cluster\"}}," +
    //                           "\"__app__\":{\"__http__\":{\"host\":\"0.0.0.0\",\"port\":8086,\"enabled\":true," +
    //                           "\"rootApi\":\"/api\"},\"api.name\":\"edge-connector\"}}";
    //        QWEConfig input = IConfig.from(jsonInput, QWEConfig.class);
    //        Assertions.assertEquals("0.0.0.0", input.getSystemConfig().getEventBusConfig().getOptions().getHost());
    //        Assertions.assertEquals(5000, input.getSystemConfig().getEventBusConfig().getOptions().getPort());
    //        JsonObject mergeJson = QWEConfig.toJson().mergeIn(input.toJson(), true);
    //        JsonObject mergeToJson = QWEConfig.mergeToJson(input);
    //
    //        JSONAssert.assertEquals(mergeJson.encode(), mergeToJson.encode(), JSONCompareMode.STRICT);
    //        QWEConfig merge = IConfig.from(mergeToJson, QWEConfig.class);
    //        JSONAssert.assertEquals(mergeJson.encode(), merge.toJson().encode(), JSONCompareMode.STRICT);
    //        QWEConfig merge1 = QWEConfig.merge(input);
    //        System.out.println(mergeJson.encodePrettily());
    //        System.out.println("===========================================");
    //        System.out.println(merge1.toJson().encodePrettily());
    //        JSONAssert.assertEquals(mergeJson.encode(), merge1.toJson().encode(), JSONCompareMode.STRICT);
    //    }
}

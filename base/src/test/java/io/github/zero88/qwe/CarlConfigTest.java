package io.github.zero88.qwe;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.qwe.CarlConfig.AppConfig;
import io.github.zero88.qwe.CarlConfig.DeployConfig;
import io.github.zero88.qwe.CarlConfig.SystemConfig;
import io.github.zero88.qwe.CarlConfig.SystemConfig.ClusterConfig;
import io.github.zero88.qwe.CarlConfig.SystemConfig.EventBusConfig;
import io.github.zero88.qwe.cluster.ClusterType;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.utils.Configs;
import io.vertx.core.json.JsonObject;

public class CarlConfigTest {

    @Test
    public void test_default() throws JSONException {
        CarlConfig from = IConfig.fromClasspath("system.json", CarlConfig.class);

        Assertions.assertEquals(CarlConfig.DEFAULT_DATADIR, from.getDataDir());
        Assertions.assertNotNull(from.getSystemConfig());

        final ClusterConfig clusterConfig = from.getSystemConfig().getClusterConfig();
        System.out.println(clusterConfig.toJson().encode());
        JSONAssert.assertEquals("{\"active\":false,\"ha\":false,\"name\":\"zbp-cluster\",\"type\":\"HAZELCAST\"," +
                                "\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}",
                                clusterConfig.toJson().encode(), JSONCompareMode.STRICT);

        final EventBusConfig eventBusConfig = from.getSystemConfig().getEventBusConfig();
        Assertions.assertNotNull(eventBusConfig);
        System.out.println(eventBusConfig.toJson().encode());
        JSONAssert.assertEquals("{\"acceptBacklog\":-1,\"clientAuth\":\"NONE\",\"clusterPingInterval\":20000," +
                                "\"clusterPingReplyInterval\":20000,\"clusterPublicPort\":-1," +
                                "\"connectTimeout\":60000,\"crlPaths\":[],\"crlValues\":[]," +
                                "\"enabledCipherSuites\":[],\"enabledSecureTransportProtocols\":[\"TLSv1\"," +
                                "\"TLSv1.1\",\"TLSv1.2\"],\"host\":\"0.0.0.0\",\"idleTimeout\":0," +
                                "\"idleTimeoutUnit\":\"SECONDS\",\"logActivity\":false,\"port\":5000," +
                                "\"receiveBufferSize\":-1,\"reconnectAttempts\":0,\"reconnectInterval\":1000," +
                                "\"reuseAddress\":true,\"reusePort\":false,\"sendBufferSize\":-1,\"soLinger\":-1," +
                                "\"ssl\":false,\"sslHandshakeTimeout\":10,\"sslHandshakeTimeoutUnit\":\"SECONDS\"," +
                                "\"tcpCork\":false,\"tcpFastOpen\":false,\"tcpKeepAlive\":false,\"tcpNoDelay\":true," +
                                "\"tcpQuickAck\":false,\"trafficClass\":-1,\"trustAll\":true,\"useAlpn\":false," +
                                "\"__delivery__\":{\"timeout\":30000,\"localOnly\":false}}",
                                eventBusConfig.toJson().encode(), JSONCompareMode.STRICT);
        final DeployConfig deployConfig = from.getDeployConfig();
        Assertions.assertNotNull(deployConfig);
        JSONAssert.assertEquals("{\"ha\":false,\"instances\":1,\"maxWorkerExecuteTime\":60000000000," +
                                "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\"," +
                                "\"worker\":false,\"workerPoolSize\":20}", deployConfig.toJson().encode(),
                                JSONCompareMode.STRICT);

        Assertions.assertNotNull(from.getAppConfig());
        Assertions.assertTrue(from.getAppConfig().isEmpty());
    }

    @Test
    public void test_init() {
        CarlConfig from = new CarlConfig();
        System.out.println(from.toJson().encodePrettily());
        Assertions.assertNotNull(from.getDataDir());
        Assertions.assertNull(from.getSystemConfig());
        Assertions.assertNotNull(from.getAppConfig());
        Assertions.assertNotNull(from.getDeployConfig());
    }

    @Test
    public void test_deserialize_simple_root() {
        JsonObject jsonObject = new JsonObject();
        CarlConfig from = IConfig.from(jsonObject, CarlConfig.class);
        Assertions.assertNotNull(from.getDataDir());
    }

    @Test
    public void test_deserialize_error_decode() {
        Assertions.assertThrows(CarlException.class, () -> IConfig.from("hello", CarlConfig.class));
    }

    @Test
    public void test_deserialize_root_having_redundant_properties() {
        Assertions.assertThrows(CarlException.class, () -> {
            String jsonStr = "{\"__redundant__\":{},\"__system__\":{\"__cluster__\":{\"active\":true,\"ha\":false," +
                             "\"name\":\"zbp-cluster\",\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\"," +
                             "\"file\":\"\",\"options\":{}}}}";
            IConfig.from(jsonStr, CarlConfig.class);
        });
    }

    @Test
    public void test_deserialize_child_with_root_having_redundant_properties() {
        String jsonStr = "{\"__redundant__\":{},\"__system__\":{\"__cluster__\":{\"active\":true,\"ha\":false," +
                         "\"name\":\"zbp-cluster\",\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\"," +
                         "\"file\":\"\",\"options\":{}}}}";
        SystemConfig cfg = IConfig.from(jsonStr, SystemConfig.class);
        Assertions.assertNotNull(cfg);
    }

    @Test
    public void test_deserialize_plain_child() {
        String jsonStr = "{\"active\":false,\"ha\":false,\"name\":\"\"," +
                         "\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}";
        SystemConfig.ClusterConfig cfg = IConfig.from(jsonStr, SystemConfig.ClusterConfig.class);
        Assertions.assertNotNull(cfg);
        Assertions.assertEquals(ClusterType.HAZELCAST, cfg.getType());
    }

    @Test
    public void test_deserialize_child_from_parent_lvl1() {
        String jsonStr = "{\"__cluster__\":{\"active\":false,\"ha\":false,\"name\":\"\"," +
                         "\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}}";
        SystemConfig.ClusterConfig cfg = IConfig.from(jsonStr, SystemConfig.ClusterConfig.class);
        Assertions.assertNotNull(cfg);
        Assertions.assertEquals(ClusterType.HAZELCAST, cfg.getType());
    }

    @Test
    public void test_deserialize_system_config() {
        String jsonStr = "{\"__cluster__\":{\"active\":false,\"ha\":false,\"name\":\"\",\"type\":\"HAZELCAST\"," +
                         "\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}," +
                         "\"__eventBus__\":{\"sendBufferSize\":-1,\"receiveBufferSize\":-1,\"trafficClass\":-1," +
                         "\"reuseAddress\":true,\"logActivity\":false,\"reusePort\":false,\"tcpNoDelay\":true," +
                         "\"tcpKeepAlive\":false,\"soLinger\":-1,\"usePooledBuffers\":false,\"idleTimeout\":0," +
                         "\"idleTimeoutUnit\":\"SECONDS\",\"ssl\":false,\"enabledCipherSuites\":[],\"crlPaths\":[]," +
                         "\"crlValues\":[],\"useAlpn\":false,\"enabledSecureTransportProtocols\":[\"TLSv1\",\"TLSv1" +
                         ".1\",\"TLSv1.2\"],\"tcpFastOpen\":false,\"tcpCork\":false,\"tcpQuickAck\":false," +
                         "\"clustered\":false,\"clusterPublicPort\":0,\"clusterPingInterval\":20000," +
                         "\"clusterPingReplyInterval\":20000,\"port\":0,\"host\":\"localhost\",\"acceptBacklog\":-1," +
                         "\"clientAuth\":\"NONE\",\"reconnectAttempts\":0,\"reconnectInterval\":1000," +
                         "\"connectTimeout\":60000,\"trustAll\":true}}";
        SystemConfig cfg = IConfig.from(jsonStr, SystemConfig.class);
        Assertions.assertNotNull(cfg);
        Assertions.assertNotNull(cfg.getClusterConfig());
        Assertions.assertNotNull(cfg.getEventBusConfig());
    }

    @Test
    public void test_deserialize_child_from_root() {
        String jsonStr =
            "{\"dataDir\": \"\", \"__system__\":{\"__cluster__\":{\"active\":false,\"ha\":false,\"name\":\"\"," +
            "\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}}}";
        SystemConfig.ClusterConfig cfg = IConfig.from(jsonStr, SystemConfig.ClusterConfig.class);
        Assertions.assertNotNull(cfg);
        Assertions.assertEquals(ClusterType.HAZELCAST, cfg.getType());
    }

    @Test
    public void test_deserialize_appCfg_from_root() throws JSONException {
        String jsonStr = "{\"__system__\":{},\"__app__\":{\"http.port\":8085}}";
        CarlConfig cfg = IConfig.from(jsonStr, CarlConfig.class);
        Assertions.assertNotNull(cfg);
        Assertions.assertNotNull(cfg.getAppConfig());
        Assertions.assertEquals(8085, cfg.getAppConfig().get("http.port"));
        JSONAssert.assertEquals("{\"http.port\":8085}", cfg.getAppConfig().toJson().encode(), JSONCompareMode.STRICT);
        Assertions.assertNotNull(cfg.getSystemConfig());
        Assertions.assertNotNull(cfg.getSystemConfig().getClusterConfig());
        Assertions.assertNotNull(cfg.getSystemConfig().getEventBusConfig());
        Assertions.assertNotNull(cfg.getDeployConfig());
        Assertions.assertNotNull(cfg.getDataDir());
    }

    @Test
    public void test_deserialize_appCfg_directly() {
        String jsonStr = "{\"__system__\":{},\"__app__\":{\"http.port\":8085}}";
        AppConfig cfg = IConfig.from(jsonStr, AppConfig.class);
        Assertions.assertNotNull(cfg);
        Assertions.assertEquals(1, cfg.size());
        Assertions.assertEquals(8085, cfg.get("http.port"));
    }

    @Test
    public void test_deserialize_appCfg_invalid_json() {
        Assertions.assertThrows(CarlException.class,
                                () -> IConfig.from("{\"__system__\":{},\"__app__\":8085}}", AppConfig.class));
    }

    @Test
    public void test_deserialize_appCfg_limitation() {
        AppConfig from = IConfig.from("{\"__system__\":{\"__cluster__\":{},\"__eventbus__\":{},\"__micro__\":{}}}",
                                      AppConfig.class);
        Assertions.assertNotNull(from);
    }

    @Test
    public void test_merge_with_empty_json() throws JSONException {
        AppConfig appconfig = IConfig.merge("{\"__app__\":{\"test\":\"1\"}}", "{\"__app__\":{}}", AppConfig.class);
        JSONAssert.assertEquals("{\"test\":\"1\"}", appconfig.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_merge_app_config() throws JSONException {
        String oldApp = "{\"__kafka__\":{\"__client__\":{\"bootstrap.servers\":[\"localhost:9092\"]}}," +
                        "\"__sql__\":{\"dialect\":\"H2\",\"__hikari__\":{\"jdbcUrl\":\"jdbc:h2:file:" +
                        "./bios-installer\",\"minimumIdle\":1,\"maximumPoolSize\":2," +
                        "\"connectionTimeout\":30000,\"idleTimeout\":180000,\"maxLifetime\":300000}}}";

        String newApp = "{\"__kafka__\":{\"__client__\":{\"bootstrap" + ".servers\":[\"localhost:9094\"]}}}";
        AppConfig merge = IConfig.merge(oldApp, newApp, AppConfig.class);
        System.out.println(merge.toJson());
        JSONAssert.assertEquals("{\"__kafka__\":{\"__client__\":{\"bootstrap.servers\":[\"localhost:9094\"]}}," +
                                "\"__sql__\":{\"dialect\":\"H2\",\"__hikari__\":{\"jdbcUrl\":\"jdbc:h2:file:" +
                                "./bios-installer\",\"minimumIdle\":1,\"maximumPoolSize\":2," +
                                "\"connectionTimeout\":30000,\"idleTimeout\":180000,\"maxLifetime\":300000}}}",
                                merge.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_merge_with_blank_value() throws JSONException {
        AppConfig appconfig = IConfig.merge("{\"__app__\":{\"test\":\"1\"}}", "{\"__app__\":{\"test\":\"" + "\"}}",
                                            AppConfig.class);
        JSONAssert.assertEquals("{\"test\":\"\"}", appconfig.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_merge_with_null_value() throws JSONException {
        AppConfig overridedAppconfig = IConfig.merge("{\"__app__\":{\"test\":\"1\"}}", "{\"__app__\":{\"test\":null}}",
                                                     AppConfig.class);

        JSONAssert.assertEquals("{\"test\":\"1\"}", overridedAppconfig.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_blank() throws JSONException {
        CarlConfig blank = CarlConfig.blank();
        Assertions.assertNotNull(blank);
        Assertions.assertNotNull(blank.getDataDir());
        Assertions.assertNotNull(blank.getAppConfig());
        Assertions.assertTrue(blank.getAppConfig().isEmpty());
        Assertions.assertNotNull(blank.getDeployConfig());
        JSONAssert.assertEquals("{\"worker\":false,\"workerPoolSize\":20," +
                                "\"maxWorkerExecuteTime\":60000000000,\"ha\":false,\"instances\":1," +
                                "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\"}",
                                blank.getDeployConfig().toJson().encode(), JSONCompareMode.STRICT);
        Assertions.assertNull(blank.getSystemConfig());
    }

    @Test
    public void test_blank_with_app_cfg() throws JSONException {
        CarlConfig blank = CarlConfig.blank(new JsonObject().put("hello", 1));
        Assertions.assertNotNull(blank);
        Assertions.assertNotNull(blank.getDataDir());
        Assertions.assertNotNull(blank.getAppConfig());
        Assertions.assertEquals(1, blank.getAppConfig().size());
        Assertions.assertEquals(1, blank.getAppConfig().get("hello"));
        Assertions.assertNotNull(blank.getDeployConfig());
        JSONAssert.assertEquals("{\"worker\":false,\"workerPoolSize\":20," +
                                "\"maxWorkerExecuteTime\":60000000000,\"ha\":false,\"instances\":1," +
                                "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\"}",
                                blank.getDeployConfig().toJson().encode(), JSONCompareMode.STRICT);
        Assertions.assertNull(blank.getSystemConfig());
    }

    @Test
    public void test_merge_with_default() throws JSONException {
        CarlConfig CarlConfig = IConfig.from(Configs.loadJsonConfig("system.json"), CarlConfig.class);
        String jsonInput = "{\"__system__\":{\"__eventBus__\":{\"clientAuth\":\"REQUIRED\",\"ssl\":true," +
                           "\"clustered\":true,\"keyStoreOptions\":{\"path\":\"eventBusKeystore.jks\"," +
                           "\"password\":\"zbpsparkEventBus\"},\"trustStoreOptions\":{\"path\":\"eventBusKeystore" +
                           ".jks\",\"password\":\"zbpsparkEventBus\"}},\"__cluster__\":{\"active\":true,\"ha\":true," +
                           "\"listenerAddress\":\"io.zero88.dashboard.connector.edge.cluster\"}}," +
                           "\"__app__\":{\"__http__\":{\"host\":\"0.0.0.0\",\"port\":8086,\"enabled\":true," +
                           "\"rootApi\":\"/api\"},\"api.name\":\"edge-connector\"}}";
        CarlConfig input = IConfig.from(jsonInput, CarlConfig.class);
        Assertions.assertEquals("0.0.0.0", input.getSystemConfig().getEventBusConfig().getOptions().getHost());
        Assertions.assertEquals(5000, input.getSystemConfig().getEventBusConfig().getOptions().getPort());
        JsonObject mergeJson = CarlConfig.toJson().mergeIn(input.toJson(), true);
        JsonObject mergeToJson = CarlConfig.mergeToJson(input);

        JSONAssert.assertEquals(mergeJson.encode(), mergeToJson.encode(), JSONCompareMode.STRICT);
        CarlConfig merge = IConfig.from(mergeToJson, CarlConfig.class);
        JSONAssert.assertEquals(mergeJson.encode(), merge.toJson().encode(), JSONCompareMode.STRICT);
        CarlConfig merge1 = CarlConfig.merge(input);
        System.out.println(mergeJson.encodePrettily());
        System.out.println("===========================================");
        System.out.println(merge1.toJson().encodePrettily());
        JSONAssert.assertEquals(mergeJson.encode(), merge1.toJson().encode(), JSONCompareMode.STRICT);
    }

}

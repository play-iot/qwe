package io.github.zero88.msa.bp;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.msa.bp.BlueprintConfig.AppConfig;
import io.github.zero88.msa.bp.BlueprintConfig.SystemConfig;
import io.github.zero88.msa.bp.cluster.ClusterType;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.utils.Configs;
import io.vertx.core.json.JsonObject;

public class BlueprintConfigTest {

//    @Test
//    public void test_default() throws JSONException {
//        BlueprintConfig from = IConfig.fromClasspath("system.json", BlueprintConfig.class);
//        System.out.println(from.toJson());
//        assertEquals(BlueprintConfig.DEFAULT_DATADIR, from.getDataDir());
//        Assert.assertNotNull(from.getSystemConfig());
//        System.out.println(from.getSystemConfig().getClusterConfig().toJson().encode());
//        JSONAssert.assertEquals("{\"active\":true,\"ha\":false,\"name\":\"zbp-cluster\",\"type\":\"HAZELCAST\"," +
//                                "\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}",
//                                from.getSystemConfig().getClusterConfig().toJson().encode(), JSONCompareMode.STRICT);
//        Assert.assertNotNull(from.getSystemConfig().getEventBusConfig());
//        JSONAssert.assertEquals("{\"acceptBacklog\":-1,\"clientAuth\":\"NONE\",\"clusterPingInterval\":20000," +
//                                "\"clusterPingReplyInterval\":20000,\"clusterPublicPort\":-1,\"clustered\":true," +
//                                "\"connectTimeout\":60000,\"crlPaths\":[],\"crlValues\":[]," +
//                                "\"enabledCipherSuites\":[],\"enabledSecureTransportProtocols\":[\"TLSv1\",\"TLSv1" +
//                                ".1\",\"TLSv1.2\"],\"host\":\"0.0.0.0\",\"idleTimeout\":0," +
//                                "\"idleTimeoutUnit\":\"SECONDS\",\"logActivity\":false,\"port\":5000," +
//                                "\"receiveBufferSize\":-1,\"reconnectAttempts\":0,\"reconnectInterval\":1000," +
//                                "\"reuseAddress\":true,\"reusePort\":false,\"sendBufferSize\":-1,\"soLinger\":-1," +
//                                "\"ssl\":false,\"tcpCork\":false,\"tcpFastOpen\":false,\"tcpKeepAlive\":false," +
//                                "\"tcpNoDelay\":true,\"tcpQuickAck\":false,\"trafficClass\":-1,\"trustAll\":true," +
//                                "\"useAlpn\":false,\"usePooledBuffers\":false,\"__delivery__\":{\"timeout\":30000," +
//                                "\"localOnly\":false}}\n", from.getSystemConfig().getEventBusConfig().toJson().encode(),
//                                JSONCompareMode.STRICT);
//        Assert.assertNotNull(from.getDeployConfig());
//        JSONAssert.assertEquals("{\"ha\":false,\"instances\":1,\"maxWorkerExecuteTime\":60000000000," +
//                                "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\",\"multiThreaded\":false," +
//                                "\"worker\":false,\"workerPoolSize\":20}", from.getDeployConfig().toJson().encode(),
//                                JSONCompareMode.STRICT);
//        Assert.assertNotNull(from.getAppConfig());
//        Assert.assertTrue(from.getAppConfig().isEmpty());
//    }

    @Test
    public void test_init() {
        BlueprintConfig from = new BlueprintConfig();
        System.out.println(from.toJson().encodePrettily());
        Assert.assertNotNull(from.getDataDir());
        Assert.assertNull(from.getSystemConfig());
        Assert.assertNotNull(from.getAppConfig());
        Assert.assertNotNull(from.getDeployConfig());
    }

    @Test
    public void test_deserialize_simple_root() {
        JsonObject jsonObject = new JsonObject();
        BlueprintConfig from = IConfig.from(jsonObject, BlueprintConfig.class);
        Assert.assertNotNull(from.getDataDir());
    }

    @Test(expected = BlueprintException.class)
    public void test_deserialize_error_decode() { IConfig.from("hello", BlueprintConfig.class); }

    @Test(expected = BlueprintException.class)
    public void test_deserialize_root_having_redundant_properties() {
        String jsonStr = "{\"__redundant__\":{},\"__system__\":{\"__cluster__\":{\"active\":true,\"ha\":false," +
                         "\"name\":\"zbp-cluster\",\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\"," +
                         "\"file\":\"\",\"options\":{}}}}";
        IConfig.from(jsonStr, BlueprintConfig.class);
    }

    @Test
    public void test_deserialize_child_with_root_having_redundant_properties() {
        String jsonStr = "{\"__redundant__\":{},\"__system__\":{\"__cluster__\":{\"active\":true,\"ha\":false," +
                         "\"name\":\"zbp-cluster\",\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\"," +
                         "\"file\":\"\",\"options\":{}}}}";
        SystemConfig cfg = IConfig.from(jsonStr, SystemConfig.class);
        Assert.assertNotNull(cfg);
    }

    @Test
    public void test_deserialize_plain_child() {
        String jsonStr = "{\"active\":false,\"ha\":false,\"name\":\"\"," +
                         "\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}";
        SystemConfig.ClusterConfig cfg = IConfig.from(jsonStr, SystemConfig.ClusterConfig.class);
        Assert.assertNotNull(cfg);
        assertEquals(ClusterType.HAZELCAST, cfg.getType());
    }

    @Test
    public void test_deserialize_child_from_parent_lvl1() {
        String jsonStr = "{\"__cluster__\":{\"active\":false,\"ha\":false,\"name\":\"\"," +
                         "\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}}";
        SystemConfig.ClusterConfig cfg = IConfig.from(jsonStr, SystemConfig.ClusterConfig.class);
        Assert.assertNotNull(cfg);
        assertEquals(ClusterType.HAZELCAST, cfg.getType());
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
        Assert.assertNotNull(cfg);
        Assert.assertNotNull(cfg.getClusterConfig());
        Assert.assertNotNull(cfg.getEventBusConfig());
    }

    @Test
    public void test_deserialize_child_from_root() {
        String jsonStr =
            "{\"dataDir\": \"\", \"__system__\":{\"__cluster__\":{\"active\":false,\"ha\":false,\"name\":\"\"," +
            "\"type\":\"HAZELCAST\",\"listenerAddress\":\"\",\"url\":\"\",\"file\":\"\",\"options\":{}}}}";
        SystemConfig.ClusterConfig cfg = IConfig.from(jsonStr, SystemConfig.ClusterConfig.class);
        Assert.assertNotNull(cfg);
        assertEquals(ClusterType.HAZELCAST, cfg.getType());
    }

    @Test
    public void test_deserialize_appCfg_from_root() throws JSONException {
        String jsonStr = "{\"__system__\":{},\"__app__\":{\"http.port\":8085}}";
        BlueprintConfig cfg = IConfig.from(jsonStr, BlueprintConfig.class);
        Assert.assertNotNull(cfg);
        Assert.assertNotNull(cfg.getAppConfig());
        assertEquals(8085, cfg.getAppConfig().get("http.port"));
        JSONAssert.assertEquals("{\"http.port\":8085}", cfg.getAppConfig().toJson().encode(), JSONCompareMode.STRICT);
        Assert.assertNotNull(cfg.getSystemConfig());
        Assert.assertNotNull(cfg.getSystemConfig().getClusterConfig());
        Assert.assertNotNull(cfg.getSystemConfig().getEventBusConfig());
        Assert.assertNotNull(cfg.getDeployConfig());
        Assert.assertNotNull(cfg.getDataDir());
    }

    @Test
    public void test_deserialize_appCfg_directly() {
        String jsonStr = "{\"__system__\":{},\"__app__\":{\"http.port\":8085}}";
        AppConfig cfg = IConfig.from(jsonStr, AppConfig.class);
        Assert.assertNotNull(cfg);
        assertEquals(1, cfg.size());
        assertEquals(8085, cfg.get("http.port"));
    }

    @Test(expected = BlueprintException.class)
    public void test_deserialize_appCfg_invalid_json() {
        IConfig.from("{\"__system__\":{},\"__app__\":8085}}", AppConfig.class);
    }

    @Test
    public void test_deserialize_appCfg_limitation() {
        AppConfig from = IConfig.from("{\"__system__\":{\"__cluster__\":{},\"__eventbus__\":{},\"__micro__\":{}}}",
                                      AppConfig.class);
        Assert.assertNotNull(from);
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
        BlueprintConfig blank = BlueprintConfig.blank();
        Assert.assertNotNull(blank);
        Assert.assertNotNull(blank.getDataDir());
        Assert.assertNotNull(blank.getAppConfig());
        Assert.assertTrue(blank.getAppConfig().isEmpty());
        Assert.assertNotNull(blank.getDeployConfig());
        JSONAssert.assertEquals("{\"worker\":false,\"multiThreaded\":false,\"workerPoolSize\":20," +
                                "\"maxWorkerExecuteTime\":60000000000,\"ha\":false,\"instances\":1," +
                                "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\"}",
                                blank.getDeployConfig().toJson().encode(), JSONCompareMode.STRICT);
        Assert.assertNull(blank.getSystemConfig());
    }

    @Test
    public void test_blank_with_app_cfg() throws JSONException {
        BlueprintConfig blank = BlueprintConfig.blank(new JsonObject().put("hello", 1));
        Assert.assertNotNull(blank);
        Assert.assertNotNull(blank.getDataDir());
        Assert.assertNotNull(blank.getAppConfig());
        assertEquals(1, blank.getAppConfig().size());
        assertEquals(1, blank.getAppConfig().get("hello"));
        Assert.assertNotNull(blank.getDeployConfig());
        JSONAssert.assertEquals("{\"worker\":false,\"multiThreaded\":false,\"workerPoolSize\":20," +
                                "\"maxWorkerExecuteTime\":60000000000,\"ha\":false,\"instances\":1," +
                                "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\"}",
                                blank.getDeployConfig().toJson().encode(), JSONCompareMode.STRICT);
        Assert.assertNull(blank.getSystemConfig());
    }

    @Test
    public void test_merge_with_default() throws JSONException {
        BlueprintConfig BlueprintConfig = IConfig.from(Configs.loadJsonConfig("system.json"), BlueprintConfig.class);
        String jsonInput = "{\"__system__\":{\"__eventBus__\":{\"clientAuth\":\"REQUIRED\",\"ssl\":true," +
                           "\"clustered\":true,\"keyStoreOptions\":{\"path\":\"eventBusKeystore.jks\"," +
                           "\"password\":\"zbpsparkEventBus\"},\"trustStoreOptions\":{\"path\":\"eventBusKeystore" +
                           ".jks\",\"password\":\"zbpsparkEventBus\"}},\"__cluster__\":{\"active\":true,\"ha\":true," +
                           "\"listenerAddress\":\"io.zero88.dashboard.connector.edge.cluster\"}}," +
                           "\"__app__\":{\"__http__\":{\"host\":\"0.0.0.0\",\"port\":8086,\"enabled\":true," +
                           "\"rootApi\":\"/api\"},\"api.name\":\"edge-connector\"}}";
        BlueprintConfig input = IConfig.from(jsonInput, BlueprintConfig.class);
        assertEquals("0.0.0.0", input.getSystemConfig().getEventBusConfig().getOptions().getHost());
        assertEquals(5000, input.getSystemConfig().getEventBusConfig().getOptions().getPort());
        JsonObject mergeJson = BlueprintConfig.toJson().mergeIn(input.toJson(), true);
        JsonObject mergeToJson = BlueprintConfig.mergeToJson(input);

        JSONAssert.assertEquals(mergeJson.encode(), mergeToJson.encode(), JSONCompareMode.STRICT);
        BlueprintConfig merge = IConfig.from(mergeToJson, BlueprintConfig.class);
        JSONAssert.assertEquals(mergeJson.encode(), merge.toJson().encode(), JSONCompareMode.STRICT);
        BlueprintConfig merge1 = BlueprintConfig.merge(input);
        System.out.println(mergeJson.encodePrettily());
        System.out.println("===========================================");
        System.out.println(merge1.toJson().encodePrettily());
        JSONAssert.assertEquals(mergeJson.encode(), merge1.toJson().encode(), JSONCompareMode.STRICT);
    }

}

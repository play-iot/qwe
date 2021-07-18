package io.zero88.qwe;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.utils.OSHelper;
import io.github.zero88.utils.SystemHelper;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.zero88.qwe.exceptions.QWEException;

@Disabled
@ExtendWith(VertxExtension.class)
public class ConfigProcessorTest {

    private ConfigProcessor processor;
    private QWEConfig qweConfig;

    @BeforeAll
    public static void beforeSuite() {
        TestHelper.setup();
    }

    @BeforeEach
    public void before(Vertx vertx, VertxTestContext testContext) {
        processor = new ConfigProcessor(vertx);

        String jsonInput = "{\"__system__\":{\"__eventBus__\":{\"clientAuth\":\"REQUIRED\",\"ssl\":true," +
                           "\"clustered\":true,\"keyStoreOptions\":{\"path\":\"eventBusKeystore.jks\"," +
                           "\"password\":\"abc123\"},\"trustStoreOptions\":{\"path\":\"eventBusKeystore" +
                           ".jks\",\"password\":\"abc123\"}},\"__cluster__\":{\"active\":true,\"ha\":true," +
                           "\"listenerAddress\":\"io.zero88.dashboard.connector.edge.cluster\"}}," +
                           "\"__app__\":{\"__http__\":{\"host\":\"0.0.0.0\",\"port\":8086,\"enabled\":true," +
                           "\"rootApi\":\"/api\", \"alpnVersions\": [ \"HTTP_2\", \"HTTP_1_1\" ]},\"api" +
                           ".name\":\"edge-connector\"}}";
        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        testContext.completeNow();
    }

    @Test
    public void test_environment_config_overridden_system_config() throws Exception {
        SystemHelper.setEnvironment(Collections.singletonMap("ZBP_APP_HTTP_HOST", "2.2.2.2"));
        System.setProperty("qwe.app.http.host", "1.1.1.1");

        String value = processor.mergeEnvVarAndSystemVar().get("qwe.app.http.host").toString();
        Assertions.assertEquals("2.2.2.2", value);
    }

    @Test
    public void test_not_have_default_and_provide_config() {
        Optional<QWEConfig> bpCfgOpt = this.processor.override(null, null, true, true);
        Assertions.assertFalse(bpCfgOpt.isPresent());
    }

    @Test
    public void test_properties_that_not_exist_in_appConfig_should_add() {
        System.setProperty("qwe.app.name", "thanh");
        System.setProperty("qwe.app.http1.abc.def", "123");

        overrideConfigThenAssert(finalResult -> {
            Assertions.assertEquals("thanh", finalResult.getAppConfig().lookup("name"));
            Assertions.assertEquals("{abc={def=123.0}}", finalResult.getAppConfig().lookup("http1").toString());
        }, true, true);
    }

    @Test
    public void test_invalid_type() {
        System.setProperty("qwe.app.http", "123");

        overrideConfigThenAssert(finalResult -> Assertions.assertEquals(
            "{host=0.0.0.0, port=8086, enabled=true, rootApi=/api, alpnVersions=[HTTP_2, HTTP_1_1]}",
            finalResult.getAppConfig().lookup("__http__").toString()), true, true);
    }

    @Test
    public void test_override_app_config() {
        System.setProperty("qwe.app.http.port", "8088");
        System.setProperty("qwe.app.http.host", "2.2.2.2");
        System.setProperty("qwe.app.http.enabled", "false");
        System.setProperty("qwe.app.http.rootApi", "/test");

        overrideConfigThenAssert(finalResult -> {
            String httpConfig = finalResult.getAppConfig().lookup("__http__").toString();
            Assertions.assertEquals(
                "{host=2.2.2.2, port=8088, enabled=false, rootApi=/test, alpnVersions=[" + "HTTP_2, HTTP_1_1]}",
                httpConfig);
        }, true, true);
    }

    @Test
    public void test_override_app_config_with_array() {
        System.setProperty("qwe.app.http.port", "8088");
        System.setProperty("qwe.app.http.host", "2.2.2.2");
        System.setProperty("qwe.app.http.enabled", "false");
        System.setProperty("qwe.app.http.rootApi", "/test");
        System.setProperty("qwe.app.http.alpnVersions", "[HTTP_2,HTTP_1_2]");

        overrideConfigThenAssert(finalResult -> {
            String httpConfig = finalResult.getAppConfig().lookup("__http__").toString();
            Assertions.assertEquals(
                "{host=2.2.2.2, port=8088, enabled=false, rootApi=/test, alpnVersions=[HTTP_2, HTTP_1_2]}", httpConfig);
        }, true, true);
    }

    @Test
    public void test_override_system_config() {
        System.setProperty("qwe.system.cluster.active", "false");
        System.setProperty("qwe.system.cluster.type", "ZOOKEEPER");
        System.setProperty("qwe.system.eventBus.port", "6000");
        System.setProperty("qwe.system.eventBus.clustered", "true");

        overrideConfigThenAssert(finalResult -> {
            Assertions.assertEquals(6000, finalResult.getBootConfig().getEventBusOptions().getPort());
            //            Assertions.assertTrue(finalResult.getSystemConfig().getEventBusConfig().getOptions()
            //            .isClustered());
            //            Assertions.assertEquals(ClusterType.ZOOKEEPER, finalResult.getSystemConfig()
            //           .getClusterManager());
            //            Assertions.assertFalse(finalResult.getSystemConfig().getClusterConfig().isActive());
        }, true, true);
    }

    @Test
    public void test_override_deploy_config() {
        System.setProperty("qwe.deploy.workerPoolSize", "50");
        System.setProperty("qwe.deploy.maxWorkerExecuteTime", "70000000000");
        System.setProperty("qwe.deploy.worker", "true");

        overrideConfigThenAssert(finalResult -> {
            try {
                JSONAssert.assertEquals("{\"ha\":false,\"instances\":1,\"maxWorkerExecuteTime\":70000000000," +
                                        "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\"," +
                                        "\"worker\":true,\"workerPoolSize\":50}",
                                        finalResult.getDeployConfig().toJson().encode(), JSONCompareMode.STRICT);
            } catch (JSONException e) {
                throw new QWEException(e);
            }
        }, true, true);
    }

    @Test
    public void test_invalid_data_type_should_be_used_default_config() {
        System.setProperty("qwe.system.cluster.active", "invalid_type");

        overrideConfigThenAssert(finalResult -> Assertions.assertTrue(finalResult.getBootConfig().isHAEnabled()), true,
                                 true);
    }

    @Test
    public void test_env_config_is_json_but_provide_config_is_primitive() {
        System.setProperty("qwe.app.test.port", "8087");
        String jsonInput1 = "{\"__app__\":{\"test\":\"anyvalue\"}}";

        qweConfig = IConfig.from(jsonInput1, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(qweConfig.toJson(), null, true, true);
        Assertions.assertTrue(finalResult.isPresent());
        Object testConfig = finalResult.get().getAppConfig().lookup("test");
        Assertions.assertNotNull(testConfig);
        Assertions.assertEquals("anyvalue", testConfig.toString());
    }

    @Test
    public void test_default_config_and_override_config() {
        System.setProperty("qwe.app.http.port", "8087");
        String jsonInput1 = "{\"__app__\":{\"__http__\":{\"host\":\"0.0.0.0\",\"port\":8086,\"enabled\":false," +
                            "\"rootApi\":\"/api\", \"alpnVersions\": [ \"HTTP_2\", \"HTTP_1_1\" ]},\"api" +
                            ".name\":\"edge-connector\"}}";
        String jsonInput2 = "{\"__app__\":{\"__http__\":{\"host\":\"1.1.1.1\",\"port\":8086," +
                            "\"rootApi\":\"/api\", \"alpnVersions\": [ \"HTTP_3\", \"HTTP_1_1\" ]},\"api" +
                            ".name\":\"edge-connector\"}}";
        qweConfig = IConfig.from(jsonInput1, QWEConfig.class);
        QWEConfig bpConfig2 = IConfig.from(jsonInput2, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(qweConfig.toJson(), bpConfig2.toJson(), true, true);
        Assertions.assertTrue(finalResult.isPresent());
        Object httpConfig = finalResult.get().getAppConfig().lookup("__http__");
        Assertions.assertNotNull(httpConfig);
        Assertions.assertEquals(
            "{host=1.1.1.1, port=8087, enabled=false, rootApi=/api, alpnVersions=[" + "HTTP_3, HTTP_1_1]}",
            httpConfig.toString());
    }

    @Test
    public void test_data_dir() {
        System.setProperty("qwe.dataDir", OSHelper.getAbsolutePathByOs("test").toString());
        overrideConfigThenAssert(finalResult -> Assertions.assertEquals(OSHelper.getAbsolutePathByOs("test"),
                                                                        finalResult.getAppConfig().dataDir()), true,
                                 true);
    }

    @Test
    public void test_double() {
        System.setProperty("qwe.app.http.port", "8087.0");
        String jsonInput = "{\"__app__\":{\"__http__\":{\"port\":8086.0}}}";
        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(null, qweConfig.toJson(), true, true);
        Assertions.assertTrue(finalResult.isPresent());
        Object httpConfig = finalResult.get().getAppConfig().lookup("__http__");
        Assertions.assertNotNull(httpConfig);
        Assertions.assertEquals("{port=8087.0}", httpConfig.toString());
    }

    @Test
    public void test_float() {
        System.setProperty("qwe.app.http.port", String.valueOf(3.4e+038));
        String jsonInput = "{\"__app__\":{\"__http__\":{\"port\":8080}}}";
        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        ((Map<String, Object>) qweConfig.getAppConfig().lookup("__http__")).put("port", (float) 3.4e+028);
        Optional<QWEConfig> finalResult = this.processor.override(null, qweConfig.toJson(), true, true);
        Assertions.assertTrue(finalResult.isPresent());
        Object httpConfig = finalResult.get().getAppConfig().lookup("__http__");
        Assertions.assertNotNull(httpConfig);
        Assertions.assertEquals("{port=3.4E38}", httpConfig.toString());
    }

    @Test
    public void test_json_array_of_json_object() {
        System.setProperty("qwe.app.https.port", "8087");
        String jsonInput =
            "{\"__app__\":{\"__https__\": [{\"host\": \"2.2.2.2\", \"port\": 8088, \"enabled\": false, " +
            "\"rootApi\": \"/test\"},{\"host\": \"2.2.2.3\", \"port\": 8089, \"enabled\": true, " +
            "\"rootApi\": \"/test1\"}]}}";
        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(null, qweConfig.toJson(), true, true);
        Assertions.assertTrue(finalResult.isPresent());
        Object httpsConfig = finalResult.get().getAppConfig().lookup("__https__");
        Assertions.assertNotNull(httpsConfig);
        Assertions.assertEquals("[{host=2.2.2.2, port=8088, enabled=false, rootApi=/test}, {host=2.2.2.3, port=8089, " +
                                "enabled=true, rootApi=/test1}]", httpsConfig.toString());
    }

    @Test
    public void test_json_array_of_primitive() {
        System.setProperty("qwe.app.https", "[abc1,def1]");
        String jsonInput = "{\"__app__\":{\"__https__\": [\"abc\", \"def\"]}}";
        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(null, qweConfig.toJson(), true, true);
        Assertions.assertTrue(finalResult.isPresent());
        Object httpsConfig = finalResult.get().getAppConfig().lookup("__https__").toString();
        Assertions.assertNotNull(httpsConfig);
        Assertions.assertEquals("[abc1, def1]", httpsConfig.toString());
    }

    @Test
    public void test_json_array_of_primitive_not_update() {
        System.setProperty("qwe.app.https.name", "[abc1,def1]");
        String jsonInput = "{\"__app__\":{\"__https__\": [\"abc\", \"def\"]}}";
        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(null, qweConfig.toJson(), true, true);
        Assertions.assertTrue(finalResult.isPresent());
        Object httpsConfig = finalResult.get().getAppConfig().lookup("__https__");
        Assertions.assertNotNull(httpsConfig);
        Assertions.assertEquals("[abc, def]", httpsConfig.toString());
    }

    @Test
    public void test_json_array_of_json_object_1() {
        System.setProperty("qwe.app.http.host.name", "[abc.net,def.net]");
        String jsonInput = "{\"__app__\":{\"__http__\":{\"host\":[{\"name\":\"abc.com\"}, {\"name\":\"def.com\"}]}}}";

        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(null, qweConfig.toJson(), true, true);
        Assertions.assertTrue(finalResult.isPresent());
        Object httpConfig = finalResult.get().getAppConfig().lookup("__http__");
        Assertions.assertNotNull(httpConfig);
        Assertions.assertEquals("{host=[{name=abc.com}, {name=def.com}]}", httpConfig.toString());
    }

    @Test
    public void test_json_array_of_primitive_1() {
        String jsonInput = "{\"__app__\":{\"__http__\":{\"host\":[\"abc.com\",\"def.com\"]}}}";
        System.setProperty("qwe.app.http.host.name", "[abc.net,def.net]");
        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(null, qweConfig.toJson(), true, true);
        Assertions.assertTrue(finalResult.isPresent());
        Object httpConfig = finalResult.get().getAppConfig().lookup("__http__");
        Assertions.assertNotNull(httpConfig);
        Assertions.assertEquals("{host=[abc.com, def.com]}", httpConfig.toString());
    }

    @Test
    public void test_override_app_config_only() {
        System.setProperty("qwe.app.http.port", "8088");
        System.setProperty("qwe.system.cluster.active", "false");
        System.setProperty("qwe.deploy.maxWorkerExecuteTime", "70000000000");
        overrideConfigThenAssert(finalResult -> {
            try {
                JSONAssert.assertEquals("{\"ha\":false,\"instances\":1,\"maxWorkerExecuteTime\":60000000000," +
                                        "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\"," +
                                        "\"worker\":false,\"workerPoolSize\":20}",
                                        finalResult.getDeployConfig().toJson().encode(), JSONCompareMode.STRICT);
            } catch (JSONException e) {
                throw new QWEException(e);
            }
            Assertions.assertTrue(finalResult.getBootConfig().isHAEnabled());
            Object httpConfig = finalResult.getAppConfig().lookup("__http__");
            Assertions.assertNotNull(httpConfig);
            Assertions.assertEquals(
                "{host=0.0.0.0, port=8088, enabled=true, rootApi=/api, alpnVersions=[" + "HTTP_2, HTTP_1_1]}",
                httpConfig.toString());
        }, true, false);
    }

    @Test
    public void test_override_other_configs_only() {
        System.setProperty("qwe.app.http.port", "8088");
        System.setProperty("qwe.system.cluster.active", "false");
        System.setProperty("qwe.deploy.maxWorkerExecuteTime", "70000000000");
        overrideConfigThenAssert(finalResult -> {
            try {
                JSONAssert.assertEquals("{\"ha\":false,\"instances\":1,\"maxWorkerExecuteTime\":70000000000," +
                                        "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\",\"worker\":false," +
                                        "\"workerPoolSize\":20}", finalResult.getDeployConfig().toJson().encode(),
                                        JSONCompareMode.STRICT);
            } catch (JSONException e) {
                throw new QWEException(e);
            }
            Assertions.assertFalse(finalResult.getBootConfig().isHAEnabled());
            Object httpConfig = finalResult.getAppConfig().lookup("__http__");
            Assertions.assertNotNull(httpConfig);
            Assertions.assertEquals(
                "{host=0.0.0.0, port=8086, enabled=true, rootApi=/api, alpnVersions=[" + "HTTP_2, HTTP_1_1]}",
                httpConfig.toString());
        }, false, true);
    }

    @Test
    public void test_override_none() {
        System.setProperty("qwe.app.http.port", "8088");
        System.setProperty("qwe.system.cluster.active", "false");
        System.setProperty("qwe.deploy.maxWorkerExecuteTime", "70000000000");
        String jsonInput = "{\"__system__\":{\"__eventBus__\":{\"clientAuth\":\"REQUIRED\",\"ssl\":true," +
                           "\"clustered\":true,\"keyStoreOptions\":{\"path\":\"eventBusKeystore.jks\"," +
                           "\"password\":\"abc123\"},\"trustStoreOptions\":{\"path\":\"eventBusKeystore" +
                           ".jks\",\"password\":\"abc123\"}},\"__cluster__\":{\"active\":true,\"ha\":true," +
                           "\"listenerAddress\":\"io.zero88.dashboard.connector.edge.cluster\"}}," +
                           "\"__app__\":{\"__http__\":{\"host\":\"0.0.0.0\",\"port\":8086,\"enabled\":true," +
                           "\"rootApi\":\"/api\", \"alpnVersions\": [ \"HTTP_2\", \"HTTP_1_1\" ]},\"api" +
                           ".name\":\"edge-connector\"}}";
        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(null, qweConfig.toJson(), false, false);
        Assertions.assertFalse(finalResult.isPresent());
    }

    @Test
    public void test_dataDir_not_available_in_system_should_use_default_config() {
        String jsonInput = "{\"dataDir\":\"/data\",\"__system__\":{\"__cluster__\":{\"active\":true," +
                           "\"ha\":true,\"type\":\"HAZELCAST\",\"options\":{}}," +
                           "\"__eventBus__\":{\"acceptBacklog\":-1,\"clientAuth\":\"REQUIRED\"," +
                           "\"clusterPingInterval\":20000,\"clusterPingReplyInterval\":20000," +
                           "\"clusterPublicPort\":-1,\"clustered\":true,\"connectTimeout\":60000,\"crlPaths\":[]," +
                           "\"crlValues\":[],\"enabledCipherSuites\":[]," +
                           "\"enabledSecureTransportProtocols\":[\"TLSv1\",\"TLSv1.1\",\"TLSv1.2\"],\"host\":\"0.0.0" +
                           ".0\",\"idleTimeout\":0,\"idleTimeoutUnit\":\"SECONDS\"," +
                           "\"keyStoreOptions\":{\"password\":\"abc123\",\"path\":\"eventBusKeystore" +
                           ".jks\"},\"logActivity\":false,\"port\":5000,\"receiveBufferSize\":-1," +
                           "\"reconnectAttempts\":0,\"reconnectInterval\":1000,\"reuseAddress\":true," +
                           "\"reusePort\":false,\"sendBufferSize\":-1,\"soLinger\":-1,\"ssl\":true,\"tcpCork\":false," +
                           "\"tcpFastOpen\":false,\"tcpKeepAlive\":false,\"tcpNoDelay\":true,\"tcpQuickAck\":false," +
                           "\"trafficClass\":-1,\"trustAll\":true," +
                           "\"trustStoreOptions\":{\"password\":\"abc123\",\"path\":\"eventBusKeystore" +
                           ".jks\"},\"useAlpn\":false,\"usePooledBuffers\":false}},\"__deploy__\":{\"ha\":false," +
                           "\"instances\":1,\"maxWorkerExecuteTime\":60000000000," +
                           "\"maxWorkerExecuteTimeUnit\":\"NANOSECONDS\",\"worker\":false," +
                           "\"workerPoolSize\":20},\"__app__\":{\"__sql__\":{\"dialect\":\"H2\"," +
                           "\"__hikari__\":{\"jdbcUrl\":\"jdbc:h2:file:/data/db/bios\"}}," +
                           "\"__installer__\":{\"auto_install\":true," +
                           "\"repository\":{\"remote\":{\"urls\":{\"java\":[{\"url\":\"http://nexus:8081/repository" +
                           "/maven-releases/\"},{\"url\":\"http://nexus:8081/repository/maven-snapshots/\"}," +
                           "{\"url\":\"http://nexus:8081/repository/maven-central/\"}]}}}," +
                           "\"builtin_app\":[{\"metadata\":{\"group_id\":\"io.zero88.edge.module\"," +
                           "\"artifact_id\":\"installer\",\"version\":\"1.0.0-SNAPSHOT\"," +
                           "\"service_name\":\"bios-installer\"},\"appConfig\":{\"__sql__\":{\"dialect\":\"H2\"," +
                           "\"__hikari__\":{\"jdbcUrl\":\"jdbc:h2:file:/data/db/bios-installer\"}}}}," +
                           "{\"metadata\":{\"group_id\":\"io.zero88.edge.module\",\"artifact_id\":\"gateway\"," +
                           "\"version\":\"1.0.0-SNAPSHOT\",\"service_name\":\"edge-gateway\"}}]}}}";
        qweConfig = IConfig.from(jsonInput, QWEConfig.class);
        Optional<QWEConfig> finalResult = this.processor.override(qweConfig.toJson(), null, true, true);
        Assertions.assertTrue(finalResult.isPresent());
        MatcherAssert.assertThat(finalResult.get().getAppConfig().dataDir().toString(),
                                 CoreMatchers.containsString("data"));
    }

    private void overrideConfigThenAssert(Consumer<QWEConfig> configConsumer, boolean overrideAppConfig,
                                          boolean overrideOtherConfigs) {
        Optional<QWEConfig> result = processor.override(qweConfig.toJson(), null, overrideAppConfig,
                                                        overrideOtherConfigs);
        configConsumer.accept(result.orElseGet(QWEConfig::create));
    }

    @AfterEach
    public void after() throws Exception {
        SystemHelper.cleanEnvironments();
        System.clearProperty("qwe.app.http.host");
        System.clearProperty("qwe.app.http.host.name");
        System.clearProperty("qwe.app.http.alpnVersions");
        System.clearProperty("qwe.app.http.port");
        System.clearProperty("qwe.app.https.port");
        System.clearProperty("qwe.app.http.enabled");
        System.clearProperty("qwe.app.http.rootApi");
        System.clearProperty("qwe.app.https");
        System.clearProperty("qwe.dataDir");
        System.clearProperty("qwe.deploy.workerPoolSize");
        System.clearProperty("qwe.deploy.worker");
        System.clearProperty("qwe.deploy.maxWorkerExecuteTime");
        System.clearProperty("qwe.system.cluster.active");
        System.clearProperty("qwe.system.cluster.type");
        System.clearProperty("qwe.system.eventBus.port");
        System.clearProperty("qwe.system.eventBus.clustered");
    }

}

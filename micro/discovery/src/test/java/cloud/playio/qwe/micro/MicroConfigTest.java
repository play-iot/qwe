package cloud.playio.qwe.micro;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.IConfig;
import cloud.playio.qwe.JsonHelper;
import cloud.playio.qwe.QWEAppConfig;
import cloud.playio.qwe.QWEConfig;
import cloud.playio.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import cloud.playio.qwe.micro.monitor.ServiceGatewayUsageMonitor;

public class MicroConfigTest {

    @Test
    public void test_blank() {
        MicroConfig def = new MicroConfig();
        Assertions.assertNull(def.lookup(ServiceDiscoveryConfig.NAME, ServiceDiscoveryConfig.class));
        Assertions.assertNull(def.lookup(ServiceGatewayConfig.NAME, ServiceGatewayConfig.class));
    }

    @Test
    public void test_parse_from_classpath() {
        MicroConfig from = IConfig.fromClasspath("discovery.json", MicroConfig.class);

        ServiceDiscoveryConfig discoveryCfg = from.lookup(ServiceDiscoveryConfig.NAME, ServiceDiscoveryConfig.class);
        Assertions.assertNotNull(discoveryCfg);
        Assertions.assertEquals("qwe.service.discovery.announce", discoveryCfg.getAnnounceAddress());
        Assertions.assertNull(discoveryCfg.getUsageAddress());
        JsonHelper.assertJson(
            new JsonObject("{\"backend-name\":\"io.vertx.servicediscovery.impl.DefaultServiceDiscoveryBackend\"}"),
            discoveryCfg.getBackendConfiguration());

        ServiceGatewayConfig gwCfg = from.lookup(ServiceGatewayConfig.NAME, ServiceGatewayConfig.class);
        Assertions.assertNotNull(gwCfg);
        Assertions.assertEquals("qwe.service.gateway.index", gwCfg.getIndexAddress());
        Assertions.assertEquals(ServiceGatewayAnnounceMonitor.class.getName(), gwCfg.getAnnounceMonitorClass());
        Assertions.assertEquals(ServiceGatewayUsageMonitor.class.getName(), gwCfg.getUsageMonitorClass());
    }

    @Test
    public void test_parse_from_root() {
        MicroConfig fromRoot = IConfig.from(IConfig.fromClasspath("discovery.json", QWEConfig.class), MicroConfig.class);
        MicroConfig fromMicro = IConfig.fromClasspath("discovery.json", MicroConfig.class);
        JsonHelper.assertJson(fromRoot.toJson(), fromMicro.toJson());
    }

    @Test
    public void test_parse_from_appConfig() {
        MicroConfig fromApp = IConfig.from(IConfig.fromClasspath("discovery.json", QWEAppConfig.class), MicroConfig.class);
        MicroConfig fromMicro = IConfig.fromClasspath("discovery.json", MicroConfig.class);
        JsonHelper.assertJson(fromMicro.toJson(), fromApp.toJson());
    }

    @Test
    public void test_parse_from_string() {
        final MicroConfig config = IConfig.from("{\"__serviceDiscovery__\":{\"announceAddress\":\"x\"," +
                                                "\"backendConfiguration\":{\"backend-name\":\"a\"," +
                                                "\"local\":false,\"more\":\"test\"},\"usageAddress\":\"y\"}}",
                                                MicroConfig.class);
        final ServiceDiscoveryConfig cfg = config.lookup(ServiceDiscoveryConfig.NAME, ServiceDiscoveryConfig.class);
        Assertions.assertNotNull(cfg);
        Assertions.assertEquals("x", cfg.getAnnounceAddress());
        Assertions.assertEquals("y", cfg.getUsageAddress());
        JsonHelper.assertJson(new JsonObject("{\"backend-name\":\"a\",\"local\":false,\"more\":\"test\"}"),
                              cfg.getBackendConfiguration());
    }

    @Test
    public void test_merge() {
        MicroConfig config = IConfig.fromClasspath("discovery.json", MicroConfig.class)
                                    .merge(IConfig.from("{\"__serviceDiscovery__\":{\"announceAddress\":\"x\"," +
                                                        "\"backendConfiguration\":{\"backend-name\":\"a\"," +
                                                        "\"local\":false,\"more\":\"test\"},\"usageAddress\":\"y\"}}",
                                                        MicroConfig.class));
        ServiceDiscoveryConfig discoveryCfg = config.lookup(ServiceDiscoveryConfig.NAME, ServiceDiscoveryConfig.class);
        Assertions.assertNotNull(discoveryCfg);
        Assertions.assertEquals("x", discoveryCfg.getAnnounceAddress());
        Assertions.assertEquals("y", discoveryCfg.getUsageAddress());
        JsonHelper.assertJson(new JsonObject("{\"backend-name\":\"a\",\"local\":false, \"more\": \"test\"}"),
                              discoveryCfg.getBackendConfiguration());
    }

}

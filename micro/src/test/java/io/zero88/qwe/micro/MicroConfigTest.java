package io.zero88.qwe.micro;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.JsonHelper;
import io.zero88.qwe.QWEAppConfig;
import io.zero88.qwe.QWEConfig;
import io.zero88.qwe.micro.MicroConfig.ServiceDiscoveryConfig;

public class MicroConfigTest {

    @Test
    public void test_default() {
        MicroConfig def = new MicroConfig();
        Assertions.assertEquals(ServiceDiscoveryConfig.SERVICE_DISCOVERY_ANNOUNCE_ADDRESS,
                                def.getDiscoveryConfig().getAnnounceAddress());
        Assertions.assertEquals(ServiceDiscoveryConfig.SERVICE_DISCOVERY_USAGE_ADDRESS,
                                def.getDiscoveryConfig().getUsageAddress());

        Assertions.assertFalse(def.getCircuitConfig().isEnabled());
        System.out.println(def.toJson().encodePrettily());
    }

    @Test
    public void test_parse() {
        MicroConfig from = IConfig.fromClasspath("micro.json", MicroConfig.class);
        JsonHelper.assertJson(new MicroConfig().toJson(), from.toJson());
    }

    @Test
    public void test_parse_from_root() {
        MicroConfig fromRoot = IConfig.from(IConfig.fromClasspath("micro.json", QWEConfig.class), MicroConfig.class);
        MicroConfig fromMicro = IConfig.fromClasspath("micro.json", MicroConfig.class);
        JsonHelper.assertJson(fromRoot.toJson(), fromMicro.toJson());
    }

    @Test
    public void test_parse_from_appConfig() {
        MicroConfig fromApp = IConfig.from(IConfig.fromClasspath("micro.json", QWEAppConfig.class), MicroConfig.class);
        MicroConfig fromMicro = IConfig.fromClasspath("micro.json", MicroConfig.class);
        System.out.println(fromApp.toJson());
        System.out.println(fromMicro.toJson());
        JsonHelper.assertJson(fromMicro.toJson(), fromApp.toJson());
    }

    @Test
    public void test_merge() {
        MicroConfig config = IConfig.fromClasspath("micro.json", MicroConfig.class)
                                    .merge(IConfig.from("{\"__serviceDiscovery__\":{\"announceAddress\":\"x\"," +
                                                        "\"backendConfiguration\":{\"backend-name\":\"a\"," +
                                                        "\"local\":false,\"more\":\"test\"},\"usageAddress\":\"y\"}}",
                                                        MicroConfig.class));
        System.out.println(config.toJson().encodePrettily());
        Assertions.assertEquals("x", config.getDiscoveryConfig().getAnnounceAddress());
        Assertions.assertEquals("y", config.getDiscoveryConfig().getUsageAddress());
        JsonHelper.assertJson(new JsonObject("{\"backend-name\":\"a\",\"local\":false, \"more\": \"test\"}"),
                              config.getDiscoveryConfig().getBackendConfiguration());
    }

}

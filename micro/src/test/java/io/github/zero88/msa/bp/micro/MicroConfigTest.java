package io.github.zero88.msa.bp.micro;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.msa.bp.BlueprintConfig;
import io.github.zero88.msa.bp.BlueprintConfig.AppConfig;
import io.github.zero88.msa.bp.IConfig;
import io.github.zero88.msa.bp.micro.MicroConfig.BackendConfig;
import io.github.zero88.msa.bp.micro.MicroConfig.LocalServiceDiscoveryConfig;
import io.github.zero88.msa.bp.micro.MicroConfig.ServiceDiscoveryConfig;

public class MicroConfigTest {

    @Test
    public void test_default() {
        MicroConfig def = new MicroConfig();
        Assertions.assertTrue(def.getDiscoveryConfig().isEnabled());
        Assertions.assertEquals(ServiceDiscoveryConfig.SERVICE_DISCOVERY_ANNOUNCE_ADDRESS,
                            def.getDiscoveryConfig().getAnnounceAddress());
        Assertions.assertEquals(ServiceDiscoveryConfig.SERVICE_DISCOVERY_USAGE_ADDRESS, def.getDiscoveryConfig().getUsageAddress());
        Assertions.assertFalse(def.getDiscoveryConfig().isLocal());
        Assertions.assertTrue(def.getDiscoveryConfig().isAutoRegistrationOfImporters());

        Assertions.assertFalse(def.getLocalDiscoveryConfig().isEnabled());
        Assertions.assertEquals(LocalServiceDiscoveryConfig.SERVICE_DISCOVERY_ANNOUNCE_LOCAL_ADDRESS,
                            def.getLocalDiscoveryConfig().getAnnounceAddress());
        Assertions.assertEquals(LocalServiceDiscoveryConfig.SERVICE_DISCOVERY_USAGE_LOCAL_ADDRESS,
                            def.getLocalDiscoveryConfig().getUsageAddress());
        Assertions.assertTrue(def.getLocalDiscoveryConfig().isLocal());
        Assertions.assertFalse(def.getLocalDiscoveryConfig().isAutoRegistrationOfImporters());

        Assertions.assertFalse(def.getCircuitConfig().isEnabled());
        System.out.println(def.toJson());
    }

    @Test
    public void test_parse() throws JSONException {
        MicroConfig from = IConfig.fromClasspath("micro.json", MicroConfig.class);
        JSONAssert.assertEquals(new MicroConfig().toJson().encode(), from.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_parse_from_root() throws JSONException {
        MicroConfig fromRoot = IConfig.from(IConfig.fromClasspath("micro.json", BlueprintConfig.class),
                                            MicroConfig.class);
        MicroConfig fromMicro = IConfig.fromClasspath("micro.json", MicroConfig.class);
        System.out.println(fromRoot.toJson());
        System.out.println(fromMicro.toJson());
        JSONAssert.assertEquals(fromRoot.toJson().encode(), fromMicro.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_parse_from_appConfig() throws JSONException {
        MicroConfig fromApp = IConfig.from(IConfig.fromClasspath("micro.json", AppConfig.class), MicroConfig.class);
        MicroConfig fromMicro = IConfig.fromClasspath("micro.json", MicroConfig.class);
        System.out.println(fromApp.toJson());
        System.out.println(fromMicro.toJson());
        JSONAssert.assertEquals(fromMicro.toJson().encode(), fromApp.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_reload_backend() {
        MicroConfig fromMicro = IConfig.fromClasspath("micro.json", MicroConfig.class);
        Assertions.assertFalse(fromMicro.getDiscoveryConfig().isLocal());
        fromMicro.getDiscoveryConfig().reloadProperty();
        Assertions.assertFalse(Boolean.parseBoolean(System.getProperty(BackendConfig.DEFAULT_SERVICE_DISCOVERY_BACKEND)));

        Assertions.assertTrue(fromMicro.getLocalDiscoveryConfig().isLocal());
        fromMicro.getLocalDiscoveryConfig().reloadProperty();
        Assertions.assertTrue(Boolean.parseBoolean(System.getProperty(BackendConfig.DEFAULT_SERVICE_DISCOVERY_BACKEND)));
    }

    @Test
    public void test_merge() throws JSONException {
        MicroConfig config = IConfig.fromClasspath("micro.json", MicroConfig.class)
                                    .merge(IConfig.from("{\"serviceName\": \"cookco\",\"__serviceDiscovery__" +
                                                        "\":{\"announceAddress\":\"x\"," +
                                                        "\"backendConfiguration\":{\"backend-name\":\"a\"," +
                                                        "\"local\":false,\"more\":\"test\"},\"usageAddress\":\"y\"}}",
                                                        MicroConfig.class));
        System.out.println(config.toJson().encodePrettily());
        Assertions.assertEquals("cookco", config.getServiceName());
        Assertions.assertEquals("x", config.getDiscoveryConfig().getAnnounceAddress());
        Assertions.assertEquals("y", config.getDiscoveryConfig().getUsageAddress());
        JSONAssert.assertEquals("{\"backend-name\":\"a\"," + "\"local\":false, \"more\": \"test\"}",
                                config.getDiscoveryConfig().getBackendConfiguration().encode(), JSONCompareMode.STRICT);
    }

}

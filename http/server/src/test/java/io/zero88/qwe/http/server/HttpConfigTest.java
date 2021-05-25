package io.zero88.qwe.http.server;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.utils.Configs;

public class HttpConfigTest {

    @Test
    public void test_default() throws JSONException {
        HttpConfig config = new HttpConfig();
        System.out.println(config.toJson().encode());
        HttpConfig fromFile = IConfig.from(Configs.loadJsonConfig("httpServer.json"), HttpConfig.class);
        JSONAssert.assertEquals(fromFile.toJson().encode(), config.toJson().encode(), JSONCompareMode.LENIENT);
    }

    @Test
    public void deserialize() {
        String jsonStr = "{\"__app__\":{\"__http__\":{\"publicHost\":\"1.1.1.1\",\"publicScheme\":\"HTTPS\"," +
                         "\"host\":\"1.1.1.1\",\"port\":9090,\"__rest__\":{\"enabled\":true,\"rootApi\":\"/xyz\"}}}}";
        HttpConfig from = IConfig.from(jsonStr, HttpConfig.class);
        Assertions.assertNotNull(from);
        Assertions.assertEquals("1.1.1.1", from.getHost());
        Assertions.assertEquals(9090, from.getPort());
        Assertions.assertEquals("/xyz", from.getRestConfig().getRootApi());
        Assertions.assertEquals("https://1.1.1.1", from.publicServerUrl());
        Assertions.assertTrue(from.getRestConfig().isEnabled());
        Assertions.assertNotNull(from.getOptions());
        Assertions.assertTrue(from.getOptions().isCompressionSupported());
        Assertions.assertTrue(from.getOptions().isDecompressionSupported());
        Assertions.assertFalse(from.getWebSocketConfig().isEnabled());
        Assertions.assertFalse(from.getHttp2Cfg().isEnabled());
    }

}

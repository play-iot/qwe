package cloud.playio.qwe.http.client;

import org.junit.jupiter.api.Test;

import cloud.playio.qwe.IConfig;
import cloud.playio.qwe.JsonHelper;

public class HttpClientConfigTest {

    @Test
    public void test_serialize_deserialize() {
        HttpClientConfig def = new HttpClientConfig();
        HttpClientConfig config = IConfig.fromClasspath("httpClient.json", HttpClientConfig.class);
        JsonHelper.assertJson(def.toJson(), config.toJson());
    }

}

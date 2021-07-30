package io.zero88.qwe.http.client;

import org.junit.jupiter.api.Test;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.JsonHelper;

public class HttpClientConfigTest {

    @Test
    public void test_serialize_deserialize() {
        HttpClientConfig def = new HttpClientConfig();
        HttpClientConfig config = IConfig.fromClasspath("httpClient.json", HttpClientConfig.class);
        JsonHelper.assertJson(def.toJson(), config.toJson());
    }

}

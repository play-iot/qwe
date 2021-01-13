package io.github.zero88.msa.bp.http.client;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.msa.bp.IConfig;
import io.github.zero88.utils.Strings;

public class HttpClientConfigTest {

    @Test
    public void test_default() throws JSONException {
        HttpClientConfig def = new HttpClientConfig();
        System.out.println(def.toJson().encodePrettily());
        HttpClientConfig config = IConfig.fromClasspath("httpClient.json", HttpClientConfig.class);
        System.out.println(config.toJson().encodePrettily());
        System.out.println(Strings.duplicate("=", 50));
        JSONAssert.assertEquals(def.toJson().encode(), config.toJson().encode(), JSONCompareMode.LENIENT);
    }

}

package io.github.zero88.msa.blueprint;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.msa.blueprint.BlueprintConfig.SystemConfig.EventBusConfig;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;

public class EventbusConfigTest {

    @Test
    public void test_default() throws JSONException {
        EventBusConfig config = new EventBusConfig();
        System.out.println("DEFAULT: " + config.toJson().encode());
        System.out.println("====================");
        EventBusConfig from = IConfig.from("{\"acceptBacklog\":-1,\"clientAuth\":\"NONE\"}", EventBusConfig.class);
        System.out.println("FROM: " + from.toJson().encode());
        JSONAssert.assertEquals(from.toJson().encode(), config.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_custom() throws JSONException {
        EventBusConfig config = new EventBusConfig();
        config.getOptions().setClientAuth(ClientAuth.REQUIRED);
        config.getOptions().setSsl(true);
        config.getOptions()
              .setKeyStoreOptions(new JksOptions().setPath("ebks.jks").setPassword("io.github.zero88EventBus"));
        config.getOptions()
              .setTrustStoreOptions(new JksOptions().setPath("ebks.jks").setPassword("io.github.zero88EventBus"));
        System.out.println("DEFAULT: " + config.toJson().encode());
        System.out.println("====================");
        EventBusConfig from = IConfig.from(
            "{\"clientAuth\":\"REQUIRED\"," + "\"ssl\":true,\"keyStoreOptions\":{\"path" + "\":\"ebks.jks\"," +
            "\"password\":\"io.github.zero88EventBus\"}," + "\"trustStoreOptions\":{\"path" + "\":\"ebks.jks\"," +
            "\"password\":\"io.github.zero88EventBus\"}}", EventBusConfig.class);
        System.out.println("FROM: " + from.toJson().encode());
        JSONAssert.assertEquals(from.toJson().encode(), config.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_merge() {
        JsonObject from = new JsonObject("{\"acceptBacklog\":-1,\"clientAuth\":\"NONE\"}");
        JsonObject to = new JsonObject("{\"acceptBacklog\":-1,\"clientAuth\":\"REQUIRED\"}");
        EventBusConfig merge = IConfig.merge(from, to, EventBusConfig.class);
        Assert.assertEquals(-1, merge.getOptions().getClusterPublicPort());
        Assert.assertEquals(-1, merge.getOptions().getAcceptBacklog());
        Assert.assertEquals(ClientAuth.REQUIRED, merge.getOptions().getClientAuth());
    }

    @Test
    public void test_merge_from_object() {
        EventBusConfig from = IConfig.from("{\"acceptBacklog\":-1,\"clientAuth\":\"NONE\"}", EventBusConfig.class);
        EventBusConfig to = IConfig.from("{\"acceptBacklog\":-1,\"clientAuth\":\"REQUIRED\"}", EventBusConfig.class);
        EventBusConfig merge = IConfig.merge(from, to, EventBusConfig.class);
        Assert.assertEquals(-1, merge.getOptions().getClusterPublicPort());
        Assert.assertEquals(-1, merge.getOptions().getAcceptBacklog());
        Assert.assertEquals(ClientAuth.REQUIRED, merge.getOptions().getClientAuth());
    }

    @Test
    public void test_parse_delivery_option_from_eventbus() {
        EventBusConfig eventBusConfig = IConfig.from(
            "{\"clientAuth\":\"REQUIRED\",\"ssl\":true,\"keyStoreOptions\":{\"path" + "\":\"ebks.jks\"," +
            "\"password\":\"io.github.zero88EventBus\"},\"trustStoreOptions\":{\"path" + "\":\"ebks.jks\"," +
            "\"password\":\"io.github.zero88EventBus\"}," + "\"__delivery__\":{\"codecName\":\"abc\"," +
            "\"timeout\": 300000, \"headers\":{\"Content-type\":\"application/json\", \"Method\":\"GET\"}}}",
            EventBusConfig.class);
        DeliveryOptions deliveryOptions = eventBusConfig.getDeliveryOptions();
        Assert.assertNotNull(deliveryOptions);
        Assert.assertEquals("abc", deliveryOptions.getCodecName());
        Assert.assertEquals(300000, deliveryOptions.getSendTimeout());
        Assert.assertNotNull(deliveryOptions.getHeaders());
        Assert.assertEquals(2, deliveryOptions.getHeaders().size());
        Assert.assertEquals("application/json", deliveryOptions.getHeaders().get("Content-type"));
        Assert.assertEquals("GET", deliveryOptions.getHeaders().get("Method"));
    }

}

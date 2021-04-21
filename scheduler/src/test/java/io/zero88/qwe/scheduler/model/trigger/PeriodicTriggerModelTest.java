package io.zero88.qwe.scheduler.model.trigger;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.TriggerKey;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.zero88.qwe.dto.JsonData;
import io.vertx.core.json.JsonObject;

public class PeriodicTriggerModelTest {

    @Test
    public void test_serialize() throws JSONException {
        PeriodicTriggerModel build = PeriodicTriggerModel.builder().name("test").intervalInSeconds(300).build();
        JsonObject entries = build.toJson();
        System.out.println(entries.encode());
        JSONAssert.assertEquals(
            "{\"type\":\"PERIODIC\",\"name\":\"test\",\"group\":\"DEFAULT\",\"intervalInSeconds\":300,\"repeat\":0}",
            entries.encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_deserialize() {
        PeriodicTriggerModel from = JsonData.from(
            "{\"type\":\"PERIODIC\",\"name\":\"test\",\"group\":\"DEFAULT\",\"intervalInSeconds\":300,\"repeat\":-1}",
            PeriodicTriggerModel.class);
        Assert.assertEquals(TriggerType.PERIODIC, from.type());
        Assert.assertEquals(TriggerKey.triggerKey("test"), from.getKey());
        Assert.assertEquals(-1, from.getRepeat());
        Assert.assertEquals(300, from.getIntervalInSeconds());
    }

}

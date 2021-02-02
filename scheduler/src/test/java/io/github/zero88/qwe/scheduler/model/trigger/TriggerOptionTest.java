package io.github.zero88.qwe.scheduler.model.trigger;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.qwe.JsonHelper;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.exceptions.CarlException;
import io.vertx.core.json.JsonObject;

public class TriggerOptionTest {

    @Test
    public void test_serialize_cron_then_build_model() throws JSONException {
        final JsonObject exp = new JsonObject(
            "{\"expression\":\"0 0 1 1/1 * ?\",\"timezone\":\"UTC\",\"type\":\"CRON\",\"repeat\":-1}");
        final TriggerOption opt = TriggerOption.builder()
                                               .type(TriggerType.CRON)
                                               .expression("0 0 1 1/1 * ?")
                                               .timezone("UTC")
                                               .build();
        JsonHelper.assertJson(exp, opt.toJson());
        final QWETriggerModel from = QWETriggerModel.from("x", "y", opt);
        Assert.assertTrue(from instanceof CronTriggerModel);
        Assert.assertEquals("x.y", from.getKey().toString());
        Assert.assertEquals(TriggerType.CRON, from.type());
    }

    @Test
    public void test_serialize_periodic_then_build_model() throws JSONException {
        final JsonObject exp = new JsonObject("{\"type\":\"PERIODIC\",\"intervalInSeconds\":10,\"repeat\":5}");
        final TriggerOption opt = TriggerOption.builder()
                                               .type(TriggerType.PERIODIC)
                                               .intervalInSeconds(10)
                                               .repeat(5)
                                               .build();
        JsonHelper.assertJson(exp, opt.toJson());
        final QWETriggerModel from = QWETriggerModel.from("a", "b", opt);
        Assert.assertTrue(from instanceof PeriodicTriggerModel);
        Assert.assertEquals("a.b", from.getKey().toString());
        Assert.assertEquals(TriggerType.PERIODIC, from.type());
        Assert.assertEquals(10, ((PeriodicTriggerModel) from).getIntervalInSeconds());
        Assert.assertEquals(5, ((PeriodicTriggerModel) from).getRepeat());
    }

    @Test(expected = CarlException.class)
    public void test_deserialize_unknown_then_build_failed() {
        JsonObject obj = new JsonObject("{\"type\":\"HEH\",\"intervalInSeconds\":10,\"repeat\":10}");
        final TriggerOption opt = JsonData.from(obj, TriggerOption.class);
        QWETriggerModel.from("a", "b", opt);
    }

    @Test
    public void test_deserialize_fulfilled_option_still_build_model_success() {
        JsonObject obj = new JsonObject("{\"type\":\"PERIODIC\",\"intervalInSeconds\":5,\"expression" +
                                        "\":\"0 0 1 1/1 * ?\",\"timezone\":\"UTC\"}");
        final TriggerOption opt = JsonData.from(obj, TriggerOption.class);
        final QWETriggerModel model = QWETriggerModel.from("zero", "88", opt);
        Assert.assertTrue(model instanceof PeriodicTriggerModel);
        Assert.assertEquals("zero.88", model.getKey().toString());
        Assert.assertEquals(TriggerType.PERIODIC, model.type());
        Assert.assertEquals(5, ((PeriodicTriggerModel) model).getIntervalInSeconds());
        Assert.assertEquals(-1, ((PeriodicTriggerModel) model).getRepeat());
    }

}

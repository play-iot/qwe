package io.github.zero88.qwe.scheduler.service;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.qwe.JsonHelper;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.scheduler.mock.JobModelCreator;
import io.github.zero88.qwe.scheduler.model.job.QWEJobModel;
import io.github.zero88.qwe.scheduler.model.trigger.CronTriggerModel;
import io.github.zero88.qwe.scheduler.model.trigger.PeriodicTriggerModel;
import io.github.zero88.qwe.scheduler.model.trigger.TriggerModel;
import io.vertx.core.json.JsonObject;

public class SchedulerRequestDataTest {

    @Test
    public void test_cron_serialize() throws JSONException {
        final QWEJobModel j1 = JobModelCreator.create("abc");
        final TriggerModel t1 = CronTriggerModel.builder().name("t1").expr("0 0/1 * 1/1 * ? *").build();
        final SchedulerRequestData req = SchedulerRequestData.builder().job(j1).trigger(t1).build();
        JsonHelper.assertJson(new JsonObject("{\"job\":{\"type\":\"EVENTBUS_JOB\",\"name\":\"abc\"," +
                                             "\"group\":\"DEFAULT\",\"process\":{\"address\":\"event.job.model" +
                                             ".test\",\"pattern\":\"REQUEST_RESPONSE\",\"action\":\"CREATE\"}," +
                                             "\"callback\":{\"address\":\"event.job.model.callback.test\"," +
                                             "\"pattern\":\"PUBLISH_SUBSCRIBE\",\"action\":\"PUBLISH\"}," +
                                             "\"forwardIfFailure\":true},\"trigger\":{\"type\":\"CRON\"," +
                                             "\"name\":\"t1\",\"group\":\"DEFAULT\",\"timezone\":\"GMT\"," +
                                             "\"expression\":\"0 0/1 * 1/1 * ? *\"}}"), req.toJson());
    }

    @Test
    public void test_cron_deserialize() {
        final QWEJobModel j1 = JobModelCreator.create("abc");
        final TriggerModel t1 = CronTriggerModel.builder().name("t1").expr("0 0/1 * 1/1 * ? *").build();
        final SchedulerRequestData data = JsonData.from("{\"job\":{\"type\":\"EVENTBUS_JOB\",\"name\":\"abc\"," +
                                                        "\"group\":\"DEFAULT\",\"process\":{\"address\":\"event.job" +
                                                        ".model" + ".test\",\"pattern\":\"REQUEST_RESPONSE\"," +
                                                        "\"action\":\"CREATE\"}," +
                                                        "\"callback\":{\"address\":\"event.job.model.callback.test\"," +
                                                        "\"pattern\":\"PUBLISH_SUBSCRIBE\",\"action\":\"PUBLISH\"}," +
                                                        "\"forwardIfFailure\":true},\"trigger\":{\"type\":\"CRON\"," +
                                                        "\"name\":\"t1\",\"group\":\"DEFAULT\",\"timezone\":\"GMT\"," +
                                                        "\"expression\":\"0 0/1 * 1/1 * ? *\"}}",
                                                        SchedulerRequestData.class);
        Assert.assertEquals(j1, data.getJob());
        Assert.assertEquals(t1, data.getTrigger());
        Assert.assertEquals(SchedulerRequestData.builder().job(j1).trigger(t1).build(), data);
    }

    @Test
    public void test_periodic_deserialize() {
        final QWEJobModel j1 = JobModelCreator.create("abc");
        final TriggerModel t1 = PeriodicTriggerModel.builder().name("tr2").intervalInSeconds(5).build();
        final SchedulerRequestData req = SchedulerRequestData.builder().job(j1).trigger(t1).build();
        System.out.println(req.toJson().encodePrettily());
        final SchedulerRequestData data = JsonData.from("{\"job\":{\"type\":\"EVENTBUS_JOB\",\"name\":\"abc\"," +
                                                        "\"group\":\"DEFAULT\",\"process\":{\"address\":\"event.job" +
                                                        ".model" + ".test\",\"pattern\":\"REQUEST_RESPONSE\"," +
                                                        "\"action\":\"CREATE\"}," +
                                                        "\"callback\":{\"address\":\"event.job.model.callback.test\"," +
                                                        "\"pattern\":\"PUBLISH_SUBSCRIBE\",\"action\":\"PUBLISH\"}," +
                                                        "\"forwardIfFailure\":true},\"trigger\":{\"type\":\"PERIODIC" +
                                                        "\",\"name\":\"tr2\",\"group\":\"DEFAULT\"," +
                                                        "\"intervalInSeconds\":5,\"repeat\":0}}",
                                                        SchedulerRequestData.class);
        Assert.assertEquals(j1, data.getJob());
        Assert.assertEquals(t1, data.getTrigger());
        Assert.assertEquals(req, data);
    }

}

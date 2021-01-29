package io.github.zero88.qwe.scheduler.model.job;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.scheduler.mock.JobModelCreator;
import io.github.zero88.qwe.scheduler.job.EventbusJob;
import io.vertx.core.json.JsonObject;

public class EventbusJobModelTest {

    @Test
    public void test_serialize() throws JSONException {
        final QWEJobModel jobModel = JobModelCreator.create("abc");
        System.out.println(jobModel.toJson());
        JSONAssert.assertEquals("{\"type\":\"EVENTBUS_JOB\",\"name\":\"abc\",\"group\":\"DEFAULT\"," +
                                "\"process\":{\"address\":\"event.job.model.test\",\"pattern\":\"REQUEST_RESPONSE\"," +
                                "\"action\":\"CREATE\"},\"callback\":{\"address\":\"event.job.model.callback.test\"," +
                                "\"pattern\":\"PUBLISH_SUBSCRIBE\",\"action\":\"PUBLISH\"},\"forwardIfFailure\":true}",
                                jobModel.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void test_to_job_detail() {
        final JobDetail jobDetail = JobModelCreator.create("xyz").toJobDetail();
        Assert.assertEquals(new JobKey("xyz"), jobDetail.getKey());
        Assert.assertEquals(EventbusJob.class, jobDetail.getJobClass());
        final Object object = jobDetail.getJobDataMap().get(QWEJobModel.JOB_DATA_KEY);
        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof EventbusJobModel);
        Assert.assertEquals(EventAction.CREATE, ((EventbusJobModel) object).getProcess().getAction());
        Assert.assertEquals("event.job.model.test", ((EventbusJobModel) object).getProcess().getAddress());
        Assert.assertEquals(EventPattern.REQUEST_RESPONSE, ((EventbusJobModel) object).getProcess().getPattern());
    }

    @Test
    public void test_deserialize() {
        EventbusJobModel jobModel = JsonData.convert(new JsonObject(
            "{\"type\":\"EVENTBUS_JOB\",\"name\":\"abc\",\"group\":\"DEFAULT\",\"process\":{\"address\":\"event.job" +
            ".model.test\",\"pattern\":\"REQUEST_RESPONSE\"," +
            "\"action\":\"CREATE\"},\"callback\":{\"address\":\"event.job" +
            ".model.callback.test\",\"pattern\":\"PUBLISH_SUBSCRIBE\",\"action\":\"PUBLISH\"}," +
            "\"forwardIfFailure\":false}"), EventbusJobModel.class);
        Assert.assertNotNull(jobModel);
        Assert.assertEquals("DEFAULT", jobModel.getKey().getGroup());
        Assert.assertEquals("abc", jobModel.getKey().getName());
        Assert.assertEquals("EVENTBUS_JOB", jobModel.type().type());
        Assert.assertFalse(jobModel.forwardIfFailure());
    }

}

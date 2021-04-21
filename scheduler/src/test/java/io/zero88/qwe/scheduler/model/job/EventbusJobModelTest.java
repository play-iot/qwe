package io.zero88.qwe.scheduler.model.job;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.scheduler.mock.JobModelCreator;
import io.vertx.core.json.JsonObject;

public class EventbusJobModelTest {

    @Test
    public void test_serialize() throws JSONException {
        final QWEJobModel jobModel = JobModelCreator.create("abc");
        System.out.println(jobModel.toJson().encodePrettily());
        JSONAssert.assertEquals("{\"type\":\"EVENTBUS_JOB\",\"name\":\"abc\",\"group\":\"DEFAULT\"," +
                                "\"process\":{\"address\":\"event.job.model.test\",\"pattern\":\"REQUEST_RESPONSE\"," +
                                "\"action\":\"CREATE\"},\"callback\":{\"address\":\"event.job.model.callback.test\"," +
                                "\"pattern\":\"PUBLISH_SUBSCRIBE\",\"action\":\"PUBLISH\"},\"forwardIfFailure\":true}",
                                jobModel.toJson().encode(), JSONCompareMode.STRICT);
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

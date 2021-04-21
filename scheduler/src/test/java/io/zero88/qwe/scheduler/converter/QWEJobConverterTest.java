package io.zero88.qwe.scheduler.converter;

import org.junit.Assert;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.JobKey;

import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventPattern;
import io.zero88.qwe.scheduler.job.EventbusJob;
import io.zero88.qwe.scheduler.job.QWEJob;
import io.zero88.qwe.scheduler.mock.JobModelCreator;
import io.zero88.qwe.scheduler.model.job.EventbusJobModel;

public class QWEJobConverterTest {

    @Test
    public void test_job_converter() {
        final JobDetail jobDetail = new QWEJobConverter().to(JobModelCreator.create("xyz"));
        Assert.assertEquals(new JobKey("xyz"), jobDetail.getKey());
        Assert.assertEquals(EventbusJob.class, jobDetail.getJobClass());
        final Object object = jobDetail.getJobDataMap().get(QWEJob.JOB_DATA_KEY);
        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof EventbusJobModel);
        Assert.assertEquals(EventAction.CREATE, ((EventbusJobModel) object).getProcess().getAction());
        Assert.assertEquals("event.job.model.test", ((EventbusJobModel) object).getProcess().getAddress());
        Assert.assertEquals(EventPattern.REQUEST_RESPONSE, ((EventbusJobModel) object).getProcess().getPattern());
    }

}

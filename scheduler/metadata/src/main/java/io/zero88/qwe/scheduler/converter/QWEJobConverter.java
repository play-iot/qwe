package io.zero88.qwe.scheduler.converter;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import io.zero88.qwe.scheduler.job.EventbusJob;
import io.zero88.qwe.scheduler.job.QWEJob;
import io.zero88.qwe.scheduler.model.job.EventbusJobModel;
import io.zero88.qwe.scheduler.model.job.QWEJobModel;

//TODO: Need registry
public class QWEJobConverter implements QuartzConverter<QWEJobModel, JobDetail> {

    @Override
    public JobDetail to(QWEJobModel jobModel) {
        if (jobModel.type() != EventbusJobModel.EVENTBUS_JOB) {
            throw new IllegalArgumentException("Unsupported job type: " + jobModel.type().type());
        }
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(QWEJob.JOB_DATA_KEY, jobModel);
        return JobBuilder.newJob(EventbusJob.class).withIdentity(jobModel.getKey()).setJobData(jobDataMap).build();
    }

}

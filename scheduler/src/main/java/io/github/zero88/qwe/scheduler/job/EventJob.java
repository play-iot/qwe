package io.github.zero88.qwe.scheduler.job;

import org.quartz.JobExecutionContext;

import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.event.ReplyEventHandler;

public final class EventJob extends AbstractVertxJob<EventJobModel> {

    @Override
    public void execute(JobExecutionContext context) {
        EventJobModel jobModel = getJobModel(context.getMergedJobDataMap());
        ReplyEventHandler handler = null;
        if (jobModel.getProcess().getPattern() == EventPattern.REQUEST_RESPONSE) {
            handler = ReplyEventHandler.builder()
                                       .system(jobModel.type().type())
                                       .address(jobModel.getProcess().getAddress())
                                       .action(jobModel.getProcess().getAction())
                                       .success(monitor(jobModel, jobModel.getCallback()))
                                       .error(errorMonitor(jobModel))
                                       .build();
        }
        EventbusClient.create(sharedData())
                      .fire(jobModel.getProcess().getAddress(), jobModel.getProcess().getPattern(),
                            jobModel.getProcess().payload(), handler);
    }

}

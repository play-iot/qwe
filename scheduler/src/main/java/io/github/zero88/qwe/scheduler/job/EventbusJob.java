package io.github.zero88.qwe.scheduler.job;

import org.quartz.JobExecutionContext;

import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.event.ReplyEventHandler;
import io.github.zero88.qwe.scheduler.model.job.EventbusJobModel;

/**
 * Eventbus job.
 * <p>
 * The execution mechanism is relied on message bus pattern
 *
 * @see EventbusClient
 */
public final class EventbusJob extends AbstractQWEJob<EventbusJobModel> {

    @Override
    public void execute(JobExecutionContext context) {
        final EventbusJobModel jobModel = queryJobModel(context);
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
                            jobModel.getProcess().getPayload(), handler);
    }

}

package io.github.zero88.qwe.scheduler.job;

import java.util.Objects;

import org.quartz.JobExecutionContext;

import io.github.zero88.qwe.event.Waybill;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.scheduler.model.job.EventbusJobModel;

import lombok.extern.slf4j.Slf4j;

/**
 * Eventbus job.
 * <p>
 * The execution mechanism is relied on message bus pattern
 *
 * @see EventbusClient
 */
@Slf4j
public final class EventbusJob extends AbstractQWEJob<EventbusJobModel> {

    @Override
    public void execute(JobExecutionContext context) {
        final EventbusJobModel jobModel = queryJobModel(context);
        final Waybill process = jobModel.getProcess();
        final Waybill callback = jobModel.getCallback();
        final EventbusClient eventbus = EventbusClient.create(sharedData());
        eventbus.request(process)
                .onErrorReturn(t -> EventMessage.error(process.getAction(), t))
                .doOnSuccess(msg -> this.publishToMonitor(context, jobModel.getKey(), msg.getStatus(), msg.getData(),
                                                          msg.getError()))
                .subscribe(msg -> {
                    if (Objects.isNull(callback) || (msg.isError() && !jobModel.forwardIfFailure())) {
                        return;
                    }
                    eventbus.publish(callback.getAddress(), EventMessage.override(msg, callback.getAction()));
                });
    }

}

package io.zero88.qwe.scheduler.job;

import java.util.Objects;

import org.quartz.JobExecutionContext;

import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.event.Waybill;
import io.zero88.qwe.scheduler.model.job.EventbusJobModel;

import lombok.extern.slf4j.Slf4j;

/**
 * Eventbus job.
 * <p>
 * The execution mechanism is relied on message bus pattern
 *
 * @see EventBusClient
 */
@Slf4j
public final class EventbusJob extends AbstractQWEJob<EventbusJobModel> {

    @Override
    public void execute(JobExecutionContext context) {
        final EventbusJobModel jobModel = queryJobModel(context);
        final Waybill process = jobModel.getProcess();
        final Waybill callback = jobModel.getCallback();
        final EventBusClient client = EventBusClient.create(sharedData());
        client.request(process.getAddress(), process.toMessage())
              .otherwise(t -> EventMessage.error(process.getAction(), t))
              .onSuccess(msg -> this.publishToMonitor(context, jobModel.getKey(), msg.getStatus(), msg.getData(),
                                                      msg.getError()))
              .onSuccess(msg -> {
                  if (Objects.isNull(callback) || (msg.isError() && !jobModel.forwardIfFailure())) {
                      return;
                  }
                  client.publish(callback.getAddress(), EventMessage.override(msg, callback.getAction()));
              });
    }

}

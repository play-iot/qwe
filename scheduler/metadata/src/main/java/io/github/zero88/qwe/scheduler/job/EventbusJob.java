package io.github.zero88.qwe.scheduler.job;

import java.util.Objects;

import org.quartz.JobExecutionContext;

import io.github.zero88.qwe.event.DeliveryEvent;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.scheduler.model.JobResult;
import io.github.zero88.qwe.scheduler.model.job.EventbusJobModel;
import io.vertx.core.json.JsonObject;

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
        final DeliveryEvent process = jobModel.getProcess();
        final DeliveryEvent callback = jobModel.getCallback();
        final EventbusClient eventbus = EventbusClient.create(sharedData());
        eventbus.request(process)
                .onErrorReturn(t -> EventMessage.error(process.getAction(), t))
                .doOnSuccess(msg -> eventbus.publish(monitorAddress(), createMonitorMsg(msg, jobModel, context)))
                .subscribe(msg -> {
                    if (Objects.isNull(callback) || (msg.isError() && !jobModel.forwardIfFailure())) {
                        return;
                    }
                    eventbus.publish(callback.getAddress(), EventMessage.override(msg, callback.getAction()));
                });
    }

    private EventMessage createMonitorMsg(EventMessage result, EventbusJobModel jobModel, JobExecutionContext context) {
        final JobResult monitor = JobResult.builder()
                                           .status(result.getStatus())
                                           .error(result.getError())
                                           .result(result.getData())
                                           .jobKey(jobModel.getKey().toString())
                                           .triggerKey(context.getTrigger().getKey().toString())
                                           .fireId(context.getFireInstanceId())
                                           .fireTime(context.getFireTime())
                                           .build();
        return EventMessage.success(EventAction.MONITOR, new JsonObject().put("result", monitor));
    }

}

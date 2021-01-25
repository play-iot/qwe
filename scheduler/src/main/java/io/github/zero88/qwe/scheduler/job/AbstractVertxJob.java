package io.github.zero88.qwe.scheduler.job;

import java.util.Objects;
import java.util.function.Consumer;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.dto.ErrorMessage;
import io.github.zero88.qwe.event.DeliveryEvent;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;

import io.github.zero88.qwe.scheduler.SchedulerConfig;

import lombok.Getter;
import lombok.experimental.Accessors;

public abstract class AbstractVertxJob<J extends JobModel> implements VertxJob<J> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    @Accessors(fluent = true)
    protected SharedDataLocalProxy sharedData;
    protected SchedulerConfig config;
    protected DeliveryEvent monitorEvent;

    @Override
    public SchedulerConfig config() { return config; }

    @Override
    public Job init(SharedDataLocalProxy sharedData, SchedulerConfig config) {
        this.sharedData = sharedData;
        this.config = config;
        this.monitorEvent = DeliveryEvent.builder()
                                         .address(config.getMonitorAddress())
                                         .pattern(EventPattern.PUBLISH_SUBSCRIBE)
                                         .action(EventAction.MONITOR)
                                         .build();
        return this;
    }

    protected Consumer<EventMessage> monitor(JobModel jobModel, DeliveryEvent callbackEvent) {
        DeliveryEvent event = Objects.isNull(callbackEvent) ? monitorEvent : callbackEvent;
        return msg -> {
            if (!jobModel.forwardIfFailure() && msg.isError()) {
                return;
            }
            logger.info("Forward JOB result to '{}'", event.getAddress());
            if (logger.isTraceEnabled()) {
                logger.trace("JOB Result: {}", msg.toJson());
            }
            EventbusClient.create(sharedData())
                          .fire(event.getAddress(), event.getPattern(), EventMessage.override(msg, event.getAction()));
        };
    }

    protected Consumer<ErrorMessage> errorMonitor(JobModel jobModel) {
        return jobModel.forwardIfFailure()
               ? null
               : msg -> EventbusClient.create(sharedData())
                                      .fire(monitorEvent.getAddress(), monitorEvent.getPattern(),
                                            EventMessage.error(monitorEvent.getAction(), null, msg));
    }

}

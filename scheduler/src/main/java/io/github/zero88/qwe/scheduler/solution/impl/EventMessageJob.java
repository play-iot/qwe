package io.github.zero88.qwe.scheduler.solution.impl;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.event.ReplyEventHandler;
import io.github.zero88.qwe.scheduler.solution.Job;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder(builderClassName = "Builder")
public class EventMessageJob implements Job {

    @NonNull
    private final EventbusClient controller;
    @NonNull
    private final EventMessage payload;
    @NonNull
    private final EventModel jobStep;
    private final EventModel postJobStep;

    @Override
    public void execute() {
        if (jobStep.getPattern() == EventPattern.REQUEST_RESPONSE) {
            ReplyEventHandler handler = ReplyEventHandler.builder()
                                                         .system("EVENT-MESSAGE-JOB")
                                                         .address(jobStep.getAddress())
                                                         .action(payload.getAction())
                                                         .success(callback(postJobStep,
                                                                           msg -> log.info("Receive message {}",
                                                                                           msg.toJson())))
                                                         .build();
            controller.fire(jobStep.getAddress(), jobStep.getPattern(), payload, handler);
        } else {
            controller.fire(jobStep.getAddress(), jobStep.getPattern(), payload);
        }
    }

    private Consumer<EventMessage> callback(EventModel step, Consumer<EventMessage> defCallback) {
        if (Objects.isNull(step)) {
            return defCallback;
        }
        return eventMessage -> controller.fire(step.getAddress(), step.getPattern(), eventMessage);
    }

    public static class Builder implements JobBuilder<EventMessageJob> {

        private EventAction action;

        /**
         * Set {@code Event Action} to init {@code EventMessage} without data
         *
         * @param action Given action
         * @return a reference to this, so the API can be used fluently
         */
        public Builder setAction(EventAction action) {
            this.action = action;
            return this;
        }

        public EventMessageJob build() {
            if (Objects.nonNull(postJobStep) && postJobStep.getPattern() == EventPattern.REQUEST_RESPONSE) {
                throw new IllegalArgumentException(
                    "Post Job Step Event model does not support " + postJobStep.getPattern() + " pattern");
            }
            if (Objects.nonNull(action) && Objects.isNull(payload)) {
                this.payload = EventMessage.initial(action);
            }
            return new EventMessageJob(this.controller, this.payload, this.jobStep, this.postJobStep);
        }

    }

}

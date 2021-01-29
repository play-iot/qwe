package io.github.zero88.qwe.scheduler;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.zero88.qwe.event.DeliveryEvent;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventContractor;
import io.github.zero88.qwe.event.EventListener;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.scheduler.model.job.EventbusJobModel;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

public class MockEventScheduler {

    public static final EventModel PROCESS_EVENT = EventModel.builder()
                                                             .pattern(EventPattern.REQUEST_RESPONSE)
                                                             .addEvents(EventAction.CREATE)
                                                             .local(true)
                                                             .address("event.job.model.test")
                                                             .build();
    public static final EventModel CALLBACK_EVENT = EventModel.builder()
                                                              .pattern(EventPattern.PUBLISH_SUBSCRIBE)
                                                              .addEvents(EventAction.PUBLISH)
                                                              .local(true)
                                                              .address("event.job.model.callback.test")
                                                              .build();


    public static class MockProcessEventSchedulerListener implements EventListener {

        private AtomicInteger count = new AtomicInteger(0);

        @Override
        public @NonNull Collection<EventAction> getAvailableEvents() {
            return PROCESS_EVENT.getEvents();
        }

        @EventContractor(action = "CREATE")
        public JsonObject increaseNumber() {
            return new JsonObject().put("count", count.getAndIncrement());
        }

    }


    public static class FailureProcessEventSchedulerListener implements EventListener {

        @Override
        public @NonNull Collection<EventAction> getAvailableEvents() {
            return PROCESS_EVENT.getEvents();
        }

        @EventContractor(action = "CREATE")
        public JsonObject increaseNumber() {
            throw new IllegalArgumentException("Failed");
        }

    }


    public static class MockJobModel {

        public static EventbusJobModel create(String name) {
            return EventbusJobModel.builder()
                                   .name(name)
                                   .process(DeliveryEvent.from(PROCESS_EVENT, EventAction.CREATE))
                                   .callback(DeliveryEvent.from(CALLBACK_EVENT, EventAction.PUBLISH))
                                   .build();
        }

        public static EventbusJobModel create(String name, @NonNull EventModel processEvent) {
            return EventbusJobModel.builder()
                                   .name(name)
                                   .process(DeliveryEvent.from(processEvent, EventAction.CREATE))
                                   .callback(DeliveryEvent.from(CALLBACK_EVENT, EventAction.PUBLISH))
                                   .forwardIfFailure(false)
                                   .build();
        }

    }

}

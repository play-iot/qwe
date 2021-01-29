package io.github.zero88.qwe.scheduler.mock;

import io.github.zero88.qwe.event.DeliveryEvent;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.scheduler.model.job.EventbusJobModel;

import lombok.NonNull;

public class JobModelCreator {

    public static EventbusJobModel create(String name) {
        return EventbusJobModel.builder()
                               .name(name)
                               .process(DeliveryEvent.from(MockEventScheduler.PROCESS_EVENT, EventAction.CREATE))
                               .callback(DeliveryEvent.from(MockEventScheduler.CALLBACK_EVENT, EventAction.PUBLISH))
                               .build();
    }

    public static EventbusJobModel create(String name, @NonNull EventModel processEvent) {
        return EventbusJobModel.builder()
                               .name(name)
                               .process(DeliveryEvent.from(processEvent, EventAction.CREATE))
                               .callback(DeliveryEvent.from(MockEventScheduler.CALLBACK_EVENT, EventAction.PUBLISH))
                               .forwardIfFailure(false)
                               .build();
    }

}

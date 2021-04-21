package io.zero88.qwe.scheduler.mock;

import io.zero88.qwe.event.Waybill;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventModel;
import io.zero88.qwe.scheduler.model.job.EventbusJobModel;

import lombok.NonNull;

public class JobModelCreator {

    public static EventbusJobModel create(String name) {
        return EventbusJobModel.builder()
                               .name(name)
                               .process(Waybill.from(MockEventScheduler.PROCESS_EVENT, EventAction.CREATE))
                               .callback(Waybill.from(MockEventScheduler.CALLBACK_EVENT, EventAction.PUBLISH))
                               .build();
    }

    public static EventbusJobModel create(String name, @NonNull EventModel processEvent) {
        return EventbusJobModel.builder()
                               .name(name)
                               .process(Waybill.from(processEvent, EventAction.CREATE))
                               .callback(Waybill.from(MockEventScheduler.CALLBACK_EVENT, EventAction.PUBLISH))
                               .forwardIfFailure(true)
                               .build();
    }

}

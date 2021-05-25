package io.zero88.qwe.http.server.mock;

import io.vertx.core.eventbus.EventBus;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.http.event.EventModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MockEventBusListener implements EventListener {

    private final EventBus eventBus;
    private final String address;

    MockEventBusListener(EventBus eventBus, EventModel model) {
        this(eventBus, model.getAddress());
    }

    public void start() {
        this.eventBus.consumer(address, msg -> handle(null, msg));
    }

}

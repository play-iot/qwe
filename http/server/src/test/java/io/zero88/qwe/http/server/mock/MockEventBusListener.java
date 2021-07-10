package io.zero88.qwe.http.server.mock;

import io.zero88.qwe.event.EventListener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MockEventBusListener implements EventListener {

    public void start() {
//        this.eventBus.consumer(address, msg -> handle(null, msg));
    }

}

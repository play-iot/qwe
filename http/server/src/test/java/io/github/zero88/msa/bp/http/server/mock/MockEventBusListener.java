package io.github.zero88.msa.bp.http.server.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.vertx.reactivex.core.eventbus.EventBus;

import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventListener;
import io.github.zero88.msa.bp.event.EventModel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MockEventBusListener implements EventListener {

    private final EventBus eventBus;
    private final String address;
    @Getter
    private List<EventAction> availableEvents = Arrays.asList(EventAction.GET_LIST, EventAction.GET_ONE,
                                                              EventAction.CREATE, EventAction.UPDATE,
                                                              EventAction.PATCH);

    MockEventBusListener(EventBus eventBus, EventModel model) {
        this(eventBus, model.getAddress());
        this.availableEvents = new ArrayList<>(model.getEvents());
    }

    public void start() {
        this.eventBus.getDelegate().consumer(address, msg -> apply(msg).subscribe());
    }

}

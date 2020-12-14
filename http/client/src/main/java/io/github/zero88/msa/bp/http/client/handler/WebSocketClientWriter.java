package io.github.zero88.msa.bp.http.client.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventContractor;
import io.github.zero88.msa.bp.event.EventListener;
import io.github.zero88.msa.bp.event.EventModel;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class WebSocketClientWriter implements EventListener {

    private final WebSocket webSocket;
    private final EventModel publisher;

    @Override
    public @NonNull Collection<EventAction> getAvailableEvents() {
        return Collections.unmodifiableList(new ArrayList<>(publisher.getEvents()));
    }

    @EventContractor(action = "SEND", returnType = boolean.class)
    public boolean send(JsonObject data) {
        webSocket.writeTextMessage(data.encode());
        return true;
    }

}

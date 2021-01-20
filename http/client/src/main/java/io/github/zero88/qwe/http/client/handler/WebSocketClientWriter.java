package io.github.zero88.qwe.http.client.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventContractor;
import io.github.zero88.qwe.event.EventListener;
import io.github.zero88.qwe.event.EventModel;
import io.reactivex.Single;
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

    @EventContractor(action = "SEND", returnType = Single.class)
    public Single<Boolean> send(JsonObject data) {
        return io.vertx.reactivex.core.http.WebSocket.newInstance(webSocket)
                                                     .rxWriteTextMessage(data.encode())
                                                     .andThen(Single.just(true))
                                                     .onErrorReturn(err -> false);
    }

}

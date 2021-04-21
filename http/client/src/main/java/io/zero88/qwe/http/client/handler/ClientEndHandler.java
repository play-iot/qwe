package io.zero88.qwe.http.client.handler;

import io.zero88.qwe.http.HostInfo;
import io.zero88.qwe.http.client.HttpClientRegistry;
import io.reactivex.functions.Action;
import io.vertx.core.Handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClientEndHandler implements Handler<Void>, Action {

    @NonNull
    private final HostInfo options;
    private final boolean isWebsocket;

    @Override
    public void handle(Void event) {
        HttpClientRegistry.getInstance().remove(options, isWebsocket);
    }

    @Override
    public void run() {
        HttpClientRegistry.getInstance().remove(options, isWebsocket);
    }

}

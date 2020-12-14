package io.github.zero88.msa.bp.http.client.handler;

import io.github.zero88.msa.bp.http.HostInfo;
import io.github.zero88.msa.bp.http.client.HttpClientRegistry;
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

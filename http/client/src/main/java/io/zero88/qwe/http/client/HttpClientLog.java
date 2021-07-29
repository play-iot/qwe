package io.zero88.qwe.http.client;

import io.zero88.qwe.LogSystem;

import lombok.NonNull;

public interface HttpClientLog extends LogSystem {

    @Override
    @NonNull
    default String function() {
        return "HTTP-C";
    }

    interface WebSocketLog extends HttpClientLog {

        @Override
        @NonNull
        default String function() {
            return "WS-C";
        }

    }

}

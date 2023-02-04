package cloud.playio.qwe.http.client;

import cloud.playio.qwe.LogSystem;

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

package io.zero88.qwe.http.server;

import lombok.NonNull;

public interface HttpSystem {

    /**
     * Defines HTTP function name
     *
     * @return function name
     */
    @NonNull String function();

    /**
     * Decor log with prefix system
     *
     * @param log log
     * @return a decorator log
     */
    default @NonNull String decor(String log) {
        return function() + "::" + log;
    }

    interface ApisSystem extends HttpSystem {

        @Override
        default String function() {
            return "APIS";
        }

    }


    interface WebSocketSystem extends HttpSystem {

        @Override
        default String function() {
            return "WS";
        }

    }


    interface WebSystem extends HttpSystem {

        @Override
        default String function() {
            return "WEB";
        }

    }


    interface UploadSystem extends HttpSystem {

        @Override
        default String function() {
            return "UPLOAD";
        }

    }


    interface DownloadSystem extends HttpSystem {

        @Override
        default String function() {
            return "DOWNLOAD";
        }

    }


    interface GatewaySystem extends HttpSystem {

        @Override
        default String function() {
            return "GATEWAY";
        }

    }

}

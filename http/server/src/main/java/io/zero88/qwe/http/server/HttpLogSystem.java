package io.zero88.qwe.http.server;

import lombok.NonNull;

public interface HttpLogSystem {

    /**
     * Defines HTTP function name
     *
     * @return function name
     */
    @NonNull String function();

    /**
     * Decor log with prefix system
     *
     * @param template log template
     * @return a decorator log
     */
    default @NonNull String decor(String template) {
        return function() + "::" + template;
    }

    interface ApisLogSystem extends HttpLogSystem {

        @Override
        default String function() {
            return "APIS";
        }

    }


    interface WebSocketLogSystem extends HttpLogSystem {

        @Override
        default String function() {
            return "WS";
        }

    }


    interface WebLogSystem extends HttpLogSystem {

        @Override
        default String function() {
            return "WEB";
        }

    }


    interface UploadLogSystem extends HttpLogSystem {

        @Override
        default String function() {
            return "UPLOAD";
        }

    }


    interface DownloadLogSystem extends HttpLogSystem {

        @Override
        default String function() {
            return "DOWNLOAD";
        }

    }


    interface GatewayLogSystem extends HttpLogSystem {

        @Override
        default String function() {
            return "GATEWAY";
        }

    }

}

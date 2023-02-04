package cloud.playio.qwe.http.server;

import cloud.playio.qwe.LogSystem;

public interface HttpSystem extends LogSystem {

    interface ApisSystem extends HttpSystem {

        @Override
        default String function() {
            return "API";
        }

    }


    interface AuthNSystem extends HttpSystem {

        @Override
        default String function() {
            return "Authentication";
        }

    }


    interface WebSocketSystem extends HttpSystem {

        @Override
        default String function() {
            return "WebSocket";
        }

    }


    interface WebSystem extends HttpSystem {

        @Override
        default String function() {
            return "Web";
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

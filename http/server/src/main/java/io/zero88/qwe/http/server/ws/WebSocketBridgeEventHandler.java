package io.zero88.qwe.http.server.ws;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.HttpSystem.WebSocketSystem;

import lombok.NonNull;

public interface WebSocketBridgeEventHandler extends Handler<BridgeEvent>, HasLogger, WebSocketSystem {

    @Override
    default Logger logger() {
        return LoggerFactory.getLogger(WebSocketBridgeEventHandler.class);
    }

    /**
     * Setup handler
     *
     * @param sharedDataProxy   The shared data proxy
     * @param socketServerPlans The list of WebSocket server plan
     * @return a reference to this for fluent API
     * @see WebSocketServerPlan
     */
    WebSocketBridgeEventHandler setup(@NonNull SharedDataLocalProxy sharedDataProxy,
                                      @NonNull List<WebSocketServerPlan> socketServerPlans);

    @Override
    default void handle(BridgeEvent event) {
        Future<Void> future;
        if (event.type() == BridgeEventType.SEND) {
            future = clientToServer(event, logEvent(event, false));
        } else if (event.type() == BridgeEventType.RECEIVE) {
            future = serverToClient(event, logEvent(event, false));
        } else {
            future = anotherEvent(event, logEvent(event, true));
        }
        event.handle(future.map(ignore -> true));
    }

    default SockJSSocket logEvent(BridgeEvent event, boolean debug) {
        final SockJSSocket socket = event.socket();
        final String msg = decor("RemoteAddr[{}] - SocketAddr[{}] - Path[{}] - Event[{}]");
        if (debug) {
            logger().debug(msg, socket.remoteAddress(), socket.localAddress(), socket.uri(), event.type());
        } else {
            logger().info(msg, socket.remoteAddress(), socket.localAddress(), socket.uri(), event.type());
        }
        return socket;
    }

    /**
     * Receive message from client then process and response/publish to one or more WebSocket client.
     * <p>
     * This method is handler of {@link BridgeEventType#SEND}
     *
     * @param event  the bridge event
     * @param socket the sockjs socket
     * @return void future
     */
    Future<Void> clientToServer(BridgeEvent event, SockJSSocket socket);

    /**
     * Publish data from server to client
     * <p>
     * This method is handler of {@link BridgeEventType#RECEIVE}
     *
     * @param event  the bridge event
     * @param socket the sockjs socket
     * @return void future
     */
    Future<Void> serverToClient(BridgeEvent event, SockJSSocket socket);

    /**
     * Handle another Bridge event rather than {@link #clientToServer(BridgeEvent, SockJSSocket)} and {@link
     * #serverToClient(BridgeEvent, SockJSSocket)}
     *
     * @param event  the bridge event
     * @param socket the sockjs socket
     */
    default Future<Void> anotherEvent(BridgeEvent event, SockJSSocket socket) {
        return Future.succeededFuture();
    }

}

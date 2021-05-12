package io.zero88.qwe.http.server.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.event.EventModel;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.http.event.WebSocketServerEventMetadata;
import io.zero88.qwe.http.server.HttpLogSystem.WebSocketLogSystem;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket event bus handler
 */
//TODO handle auth with socket header
@Slf4j
public class WebSocketBridgeEventHandler implements Handler<BridgeEvent>, WebSocketLogSystem {

    private final Map<String, WebSocketServerEventMetadata> metadataByListener = new HashMap<>();
    private final WebSocketEventExecutor executor;

    public WebSocketBridgeEventHandler(@NonNull SharedDataLocalProxy sharedData,
                                       @NonNull List<WebSocketServerEventMetadata> addressMap) {
        this.executor = new WebSocketEventExecutor(sharedData);
        addressMap.forEach(this::initMetadata);
    }

    private void initMetadata(WebSocketServerEventMetadata metadata) {
        EventModel eventModel = Objects.isNull(metadata.getListener())
                                ? metadata.getPublisher()
                                : metadata.getListener();
        metadataByListener.put(eventModel.getAddress(), metadata);
    }

    @Override
    public void handle(BridgeEvent event) {
        if (event.type() == BridgeEventType.SEND) {
            clientToServer(event);
        } else {
            logEvent(event, true);
        }
        event.tryComplete(true);
    }

    private void logEvent(BridgeEvent event, boolean debug) {
        String msg = decor("RemoteAddr:'{}'-SocketAddr:'{}'-Path:'{}'-Event:'{}'");
        SockJSSocket socket = event.socket();
        if (debug) {
            log.debug(msg, socket.remoteAddress(), socket.localAddress(), socket.uri(), event.type());
        } else {
            log.info(msg, socket.remoteAddress(), socket.localAddress(), socket.uri(), event.type());
        }
    }

    private void clientToServer(BridgeEvent event) {
        logEvent(event, false);
        SockJSSocket socket = event.socket();
        socket.exceptionHandler(t -> log.error(decor("WebSocket server raise error"), t));
        String address = event.getRawMessage().getString("address");
        WebSocketServerEventMetadata metadata = this.metadataByListener.get(address);
        if (Objects.isNull(metadata)) {
            socket.close(HttpResponseStatus.NOT_FOUND.code(), decor("Unsupported address " + address));
            return;
        }
        try {
            log.info(decor("Redirect message from address: {}"), address);
            executor.execute(WebSocketEventMessage.from(event.getRawMessage()), metadata, handleMessage(socket));
        } catch (CarlException e) {
            handleMessage(socket).accept(EventMessage.error(EventAction.RETURN, e));
        }
    }

    //TODO May override consumer for message
    protected Consumer<EventMessage> handleMessage(SockJSSocket socket) {
        return message -> socket.write(message.toJson().encode());
    }

}

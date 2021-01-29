package io.github.zero88.qwe.http.server.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.http.event.WebSocketServerEventMetadata;
import io.github.zero88.utils.Strings;
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
public class WebSocketBridgeEventHandler implements Handler<BridgeEvent> {

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
        String msg = "Websocket::Event: {} - Remote: {} - Path: {} - Id: {}";
        SockJSSocket socket = event.socket();
        if (debug) {
            log.debug(msg, event.type(), socket.remoteAddress(), socket.uri(), socket.writeHandlerID());
        } else {
            log.info(msg, event.type(), socket.remoteAddress(), socket.uri(), socket.writeHandlerID());
        }
    }

    private void clientToServer(BridgeEvent event) {
        logEvent(event, false);
        SockJSSocket socket = event.socket();
        socket.exceptionHandler(t -> log.error("WEBSOCKET::Backend error", t));
        String address = event.getRawMessage().getString("address");
        WebSocketServerEventMetadata metadata = this.metadataByListener.get(address);
        if (Objects.isNull(metadata)) {
            String errorMsg = Strings.format("Address {0} is not found", address);
            socket.close(HttpResponseStatus.NOT_FOUND.code(), errorMsg);
            return;
        }
        try {
            log.info("WEBSOCKET::Redirect message from address: {}", address);
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

package io.github.zero88.qwe.http.client;

import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.http.client.handler.WebSocketClientWriter;
import io.github.zero88.qwe.http.client.handler.WebSocketConnectErrorHandler;
import io.github.zero88.qwe.http.client.handler.WebSocketResponseDispatcher;
import io.github.zero88.qwe.http.client.handler.WebSocketResponseErrorHandler;
import io.github.zero88.qwe.http.event.WebSocketClientEventMetadata;
import io.reactivex.Single;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.json.JsonObject;

final class WebSocketClientDelegateImpl extends ClientDelegate implements WebSocketClientDelegate {

    private final EventbusClient eventbus;

    WebSocketClientDelegateImpl(Vertx vertx, HttpClientConfig config) {
        super(vertx, config);
        this.eventbus = EventbusClient.create(vertx);
    }

    @Override
    public Single<EventMessage> open(WebSocketClientEventMetadata metadata, MultiMap headers) {
        final WebSocketConnectOptions options = new WebSocketConnectOptions().setHeaders(headers)
                                                                             .setURI(metadata.getPath());
        return getRx().rxWebSocket(options)
                      .map(webSocket -> onSuccess(webSocket, metadata))
                      .onErrorReturn(WebSocketConnectErrorHandler.create(getHostInfo(), eventbus,
                                                                         getHandlerConfig().getWebSocketConnectErrorHandlerCls()));
    }

    private EventMessage onSuccess(io.vertx.reactivex.core.http.WebSocket webSocket,
                                   WebSocketClientEventMetadata metadata) {
        logger.info("Websocket to {} is connected", getHostInfo().toJson());
        WebSocket ws = webSocket.getDelegate();
        eventbus.register(metadata.getPublisher(), new WebSocketClientWriter(ws, metadata.getPublisher()));
        EventModel listener = metadata.getListener();
        ws.handler(WebSocketResponseDispatcher.create(eventbus, listener,
                                                      getHandlerConfig().getWebSocketResponseDispatcherCls()));
        ws.exceptionHandler(
            WebSocketResponseErrorHandler.create(eventbus, listener, getHandlerConfig().getWebSocketErrorHandlerCls()));
        return EventMessage.success(EventAction.parse("OPEN"),
                                    new JsonObject().put("binaryHandlerID", webSocket.binaryHandlerID())
                                                    .put("textHandlerID", webSocket.textHandlerID()));
    }

    @Override
    public EventbusClient getEventbus() {
        return eventbus;
    }

}

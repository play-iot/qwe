package io.zero88.qwe.http.client;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.event.EventModel;
import io.zero88.qwe.http.client.handler.WebSocketClientWriter;
import io.zero88.qwe.http.client.handler.WebSocketConnectErrorHandler;
import io.zero88.qwe.http.client.handler.WebSocketResponseDispatcher;
import io.zero88.qwe.http.client.handler.WebSocketResponseErrorHandler;
import io.zero88.qwe.http.event.WebSocketClientEventMetadata;

final class WebSocketClientDelegateImpl extends ClientDelegate implements WebSocketClientDelegate {

    private final EventBusClient eventbus;

    WebSocketClientDelegateImpl(Vertx vertx, HttpClientConfig config) {
        super(vertx, config);
        //FIXME create client
        this.eventbus = EventBusClient.create(SharedDataLocalProxy.create(vertx, "wtf"));
    }

    @Override
    public Future<EventMessage> open(WebSocketClientEventMetadata metadata, MultiMap headers) {
        final WebSocketConnectOptions options = new WebSocketConnectOptions().setHeaders(headers)
                                                                             .setURI(metadata.getPath());
        return get().webSocket(options)
                    .map(webSocket -> onSuccess(webSocket, metadata))
                    .otherwise(WebSocketConnectErrorHandler.create(getHostInfo(), eventbus,
                                                                   getHandlerConfig().getWebSocketConnectErrorHandlerCls()));
    }

    private EventMessage onSuccess(WebSocket webSocket, WebSocketClientEventMetadata metadata) {
        logger.info("Websocket to {} is connected", getHostInfo().toJson());
        eventbus.register(metadata.getPublisher().getAddress(), new WebSocketClientWriter(webSocket));
        EventModel listener = metadata.getListener();
        webSocket.handler(WebSocketResponseDispatcher.create(eventbus, listener,
                                                             getHandlerConfig().getWebSocketResponseDispatcherCls()));
        webSocket.exceptionHandler(
            WebSocketResponseErrorHandler.create(eventbus, listener, getHandlerConfig().getWebSocketErrorHandlerCls()));
        return EventMessage.success(EventAction.parse("OPEN"),
                                    new JsonObject().put("binaryHandlerID", webSocket.binaryHandlerID())
                                                    .put("textHandlerID", webSocket.textHandlerID()));
    }

    @Override
    public EventBusClient getEventbus() {
        return eventbus;
    }

}

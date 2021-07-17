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
import io.zero88.qwe.event.EventDirection;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.client.handler.WebSocketClientPlan;
import io.zero88.qwe.http.client.handler.WebSocketClientWriter;
import io.zero88.qwe.http.client.handler.WebSocketConnectErrorHandler;
import io.zero88.qwe.http.client.handler.WebSocketResponseDispatcher;
import io.zero88.qwe.http.client.handler.WebSocketResponseErrorHandler;

import lombok.Getter;
import lombok.experimental.Accessors;

final class WebSocketClientDelegateImpl extends ClientDelegate implements WebSocketClientDelegate {

    @Getter
    @Accessors(fluent = true)
    private final EventBusClient transporter;

    WebSocketClientDelegateImpl(Vertx vertx, HttpClientConfig config) {
        super(vertx, config);
        //FIXME: create client
        this.transporter = EventBusClient.create(SharedDataLocalProxy.create(vertx, "wtf"));
    }

    @Override
    public Future<EventMessage> open(WebSocketClientPlan metadata, MultiMap headers) {
        final WebSocketConnectOptions options = new WebSocketConnectOptions().setHeaders(headers)
                                                                             .setURI(metadata.getPath());
        return get().webSocket(options)
                    .map(webSocket -> onSuccess(webSocket, metadata))
                    .otherwise(WebSocketConnectErrorHandler.create(getHostInfo(), transporter,
                                                                   getHandlerConfig().getWebSocketConnectErrorHandlerCls()));
    }

    private EventMessage onSuccess(WebSocket webSocket, WebSocketClientPlan metadata) {
        logger().info("WebSocket to {} is connected", getHostInfo().toJson());
        transporter.register(metadata.outbound().getAddress(), new WebSocketClientWriter(webSocket));
        EventDirection inbound = metadata.inbound();
        webSocket.handler(WebSocketResponseDispatcher.create(transporter, inbound,
                                                             getHandlerConfig().getWebSocketResponseDispatcherCls()));
        webSocket.exceptionHandler(WebSocketResponseErrorHandler.create(transporter, inbound,
                                                                        getHandlerConfig().getWebSocketErrorHandlerCls()));
        return EventMessage.success(EventAction.parse("OPEN"),
                                    new JsonObject().put("binaryHandlerID", webSocket.binaryHandlerID())
                                                    .put("textHandlerID", webSocket.textHandlerID()));
    }

}

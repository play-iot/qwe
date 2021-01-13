package io.github.zero88.msa.bp.http.client;

import io.github.zero88.msa.bp.component.SharedDataDelegate;
import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.http.client.HttpClientConfig.HandlerConfig;
import io.github.zero88.msa.bp.http.client.handler.ClientEndHandler;
import io.github.zero88.msa.bp.http.client.handler.WebSocketClientWriter;
import io.github.zero88.msa.bp.http.client.handler.WebSocketConnectErrorHandler;
import io.github.zero88.msa.bp.http.client.handler.WebSocketResponseDispatcher;
import io.github.zero88.msa.bp.http.client.handler.WebSocketResponseErrorHandler;
import io.github.zero88.msa.bp.http.event.WebSocketClientEventMetadata;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;

final class WebSocketClientDelegateImpl extends ClientDelegate implements WebSocketClientDelegate {

    private final EventbusClient controller;

    WebSocketClientDelegateImpl(Vertx vertx, HttpClientConfig config) {
        super(vertx, config);
        this.controller = SharedDataDelegate.getEventController(vertx, WebSocketClientDelegate.class.getName());
    }

    @Override
    public void open(WebSocketClientEventMetadata metadata, MultiMap headers) {
        WebSocketConnectOptions options = new WebSocketConnectOptions().setHeaders(headers).setURI(metadata.getPath());
        get().webSocket(options, ar -> {
            final HandlerConfig handler = getHandlerConfig();
            if (ar.succeeded()) {
                logger.info("Websocket to {} is connected", getHostInfo().toJson());
                final WebSocket ws = ar.result();
                controller.register(metadata.getPublisher(), new WebSocketClientWriter(ws, metadata.getPublisher()));
                EventModel listener = metadata.getListener();
                ws.handler(
                    WebSocketResponseDispatcher.create(controller, listener, handler.getWebSocketResponseDispatcherCls()));
                ws.exceptionHandler(
                    WebSocketResponseErrorHandler.create(controller, listener, handler.getWebSocketErrorHandlerCls()));
                ws.closeHandler(new ClientEndHandler(getHostInfo(), true));
            } else {
                WebSocketConnectErrorHandler.create(getHostInfo(), controller, handler.getWebSocketConnectErrorHandlerCls())
                                            .handle(ar.cause());
            }
        });
    }

    @Override
    public void asyncOpen(WebSocketClientEventMetadata metadata, MultiMap headers) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public EventbusClient getEventClient() {
        return controller;
    }

}

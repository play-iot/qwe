package io.github.zero88.msa.bp.http.client;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.github.zero88.msa.bp.component.SharedDataDelegate;
import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.exceptions.InitializerError;
import io.github.zero88.msa.bp.http.client.HttpClientConfig.HandlerConfig;
import io.github.zero88.msa.bp.http.client.handler.ClientEndHandler;
import io.github.zero88.msa.bp.http.client.handler.WebSocketClientWriter;
import io.github.zero88.msa.bp.http.client.handler.WsConnectErrorHandler;
import io.github.zero88.msa.bp.http.client.handler.WsLightResponseDispatcher;
import io.github.zero88.msa.bp.http.client.handler.WsResponseErrorHandler;
import io.github.zero88.msa.bp.http.event.WebSocketClientEventMetadata;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;

final class WebSocketClientDelegateImpl extends ClientDelegate implements WebSocketClientDelegate {

    private final int connTimeout;
    private final EventbusClient controller;

    WebSocketClientDelegateImpl(Vertx vertx, HttpClientConfig config) {
        super(vertx, config);
        this.connTimeout = config.getOptions().getConnectTimeout();
        this.controller = SharedDataDelegate.getEventController(vertx, WebSocketClientDelegate.class.getName());
    }

    @Override
    public void open(WebSocketClientEventMetadata metadata, MultiMap headers) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> ref = new AtomicReference<>();
        HandlerConfig handler = getHandlerConfig();
        get().websocket(metadata.getPath(), headers, ws -> {
            latch.countDown();
            logger.info("Websocket to {} is connected", getHostInfo().toJson());
            controller.register(metadata.getPublisher(), new WebSocketClientWriter(ws, metadata.getPublisher()));
            EventModel listener = metadata.getListener();
            ws.handler(
                WsLightResponseDispatcher.create(controller, listener, handler.getWsLightResponseHandlerClass()));
            ws.exceptionHandler(WsResponseErrorHandler.create(controller, listener, handler.getWsErrorHandlerClass()));
            ws.closeHandler(new ClientEndHandler(getHostInfo(), true));
        }, t -> {
            ref.set(t);
            latch.countDown();
        });
        try {
            boolean r = latch.await(connTimeout + 100L, TimeUnit.MILLISECONDS);
            final Throwable error = ref.get();
            if (r && Objects.isNull(error)) {
                return;
            }
            WsConnectErrorHandler.create(getHostInfo(), controller, handler.getWsConnectErrorHandlerClass())
                                 .handle(error);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InitializerError("Interrupted thread when open websocket connection", e);
        }
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

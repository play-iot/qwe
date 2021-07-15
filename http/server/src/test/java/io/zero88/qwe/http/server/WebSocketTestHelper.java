package io.zero88.qwe.http.server;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.zero88.utils.Urls;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.unit.TestContext;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.event.EventMessage;

import lombok.NonNull;

public interface WebSocketTestHelper extends HttpServerTestHelper {

    default WebSocket setupWSClient(TestContext context, String path, Function<WebSocket, WebSocket> decorator) {
        return setupWSClient(context, path, decorator, context::fail);
    }

    default WebSocket setupWSClient(TestContext context, String path, Function<WebSocket, WebSocket> decorator,
                                    Consumer<Throwable> error) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<WebSocket> wsReference = new AtomicReference<>();
        Function<WebSocket, WebSocket> fun = Optional.ofNullable(decorator)
                                                     .orElse(Function.identity())
                                                     .andThen(ws -> ws.exceptionHandler(error::accept));
        client().webSocket(wsOpt(requestOptions().setURI(Urls.combinePath(path, "websocket"))))
                .onFailure(error::accept)
                .map(fun)
                .onSuccess(ws -> {
                    wsReference.set(ws);
                    latch.countDown();
                });
        try {
            context.assertTrue(latch.await(TestHelper.TEST_TIMEOUT_SEC, TimeUnit.SECONDS), "WS Timeout");
        } catch (InterruptedException e) {
            context.fail(e);
        }
        return wsReference.get();
    }

    default WebSocket wsRegister(WebSocket webSocket, String address) {
        return wsWrite(webSocket, createWsMsg(address, null, BridgeEventType.REGISTER).toBuffer());
    }

    default WebSocket wsSend(WebSocket webSocket, String address, EventMessage body) {
        return wsWrite(webSocket, createWsMsg(address, body, BridgeEventType.SEND).toBuffer());
    }

    default WebSocket wsWrite(WebSocket webSocket, JsonObject body) {
        return wsWrite(webSocket, body.toBuffer());
    }

    default WebSocket wsWrite(WebSocket webSocket, Buffer body) {
        webSocket.writeBinaryMessage(body);
        return webSocket;
    }

    default JsonObject createWsMsg(String address, EventMessage body, BridgeEventType type) {
        return new JsonObject().put("type", type == BridgeEventType.RECEIVE ? "rec" : type.name().toLowerCase())
                               .put("address", address)
                               .put("body", body);
    }

    default WebSocketConnectOptions wsOpt(@NonNull RequestOptions opt) {
        return new WebSocketConnectOptions(opt.setMethod(HttpMethod.GET).toJson());
    }

}

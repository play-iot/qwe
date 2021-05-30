package io.zero88.qwe.http.server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.http.WebSocketFrame;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.zero88.qwe.ComponentTestHelper;
import io.zero88.qwe.EventBusHelper;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.HostInfo;
import io.zero88.qwe.http.client.HttpClientDelegate;
import io.zero88.qwe.http.server.ws.WebSocketEventMessage;

import lombok.NonNull;

@RunWith(VertxUnitRunner.class)
public abstract class HttpServerTestBase implements ComponentTestHelper {

    protected static final String DEFAULT_HOST = "127.0.0.1";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    protected Vertx vertx;
    protected HttpConfig httpConfig;
    protected HttpClient client;
    protected RequestOptions requestOptions;

    @BeforeClass
    public static void beforeSuite() { TestHelper.setup(); }

    @Override
    public Path testDir() {
        return folder.getRoot().toPath();
    }

    @Before
    public void before(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        httpConfig = IConfig.fromClasspath(httpConfigFile(), HttpConfig.class);
        httpConfig.setHost(DEFAULT_HOST);
        httpConfig.setPort(TestHelper.getRandomPort());
        client = vertx.createHttpClient(createClientOptions());
        requestOptions = new RequestOptions().setHost(DEFAULT_HOST).setPort(httpConfig.getPort());
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    protected String httpConfigFile() {
        return "httpServer.json";
    }

    protected void enableWebsocket() {
        this.httpConfig.getRestConfig().setEnabled(false);
        this.httpConfig.getWebSocketConfig().setEnabled(true);
    }

    protected HttpClientOptions createClientOptions() {
        return new HttpClientOptions().setConnectTimeout(TestHelper.TEST_TIMEOUT_SEC);
    }

    protected void assertRestByClient(TestContext context, HttpMethod method, String path, int codeExpected,
                                      JsonObject bodyExpected, Customization... customizations) {
        assertRestByClient(context, method, path, codeExpected, bodyExpected, JSONCompareMode.STRICT, customizations);
    }

    protected void assertRestByClient(TestContext context, HttpMethod method, String path, int codeExpected,
                                      JsonObject bodyExpected, JSONCompareMode mode, Customization... customizations) {
        assertRestByClient(context, method, path, null, ExpectedResponse.builder()
                                                                        .expected(bodyExpected)
                                                                        .code(codeExpected)
                                                                        .customizations(customizations)
                                                                        .mode(mode)
                                                                        .build());
    }

    protected void assertRestByClient(TestContext context, HttpMethod method, String path, RequestData requestData,
                                      int codeExpected, JsonObject bodyExpected, Customization... customizations) {
        assertRestByClient(context, method, path, requestData, ExpectedResponse.builder()
                                                                               .expected(bodyExpected)
                                                                               .code(codeExpected)
                                                                               .customizations(customizations)
                                                                               .mode(JSONCompareMode.STRICT)
                                                                               .build());
    }

    protected void assertRestByClient(TestContext context, HttpMethod method, String path, RequestData requestData,
                                      ExpectedResponse expected) {
        Async async = context.async(expected.hasAfter() ? 2 : 1);
        HttpClientDelegate.create(vertx, HostInfo.from(requestOptions))
                          .request(path, method, requestData)
                          .onSuccess(resp -> expected._assert(context, async, resp))
                          .onFailure(context::fail)
                          .eventually(v -> {
                              TestHelper.testComplete(async);
                              return Future.succeededFuture();
                          });
    }

    protected Future<ResponseData> restRequest(TestContext context, HttpMethod method, String path,
                                               RequestData requestData) {
        Async async = context.async();
        return HttpClientDelegate.create(vertx, HostInfo.from(requestOptions))
                                 .request(path, method, requestData)
                                 .eventually(v -> {
                                     TestHelper.testComplete(async);
                                     return Future.succeededFuture();
                                 });
    }

    protected HttpServer startServer(TestContext context, HttpServerRouter httpRouter) {
        return deploy(vertx, context, httpConfig.toJson(), new HttpServerProvider(httpRouter));
    }

    protected void startServer(TestContext context, HttpServerRouter httpRouter, Consumer<Throwable> consumer) {
        deployFailed(vertx, context, httpConfig.toJson(), new HttpServerProvider(httpRouter), consumer);
    }

    protected JsonObject notFoundResponse(int port, String path) {
        return new JsonObject().put("message", "Resource not found")
                               .put("uri", Strings.format("http://{0}:{1}{2}", DEFAULT_HOST, port, path));
    }

    protected void testComplete(Async async) {
        testComplete(async, "");
    }

    private void testComplete(Async async, String msgEvent) {
        TestHelper.testComplete(async, msgEvent, closeClient());
    }

    private Handler<Void> closeClient() {
        return e -> client.close();
    }

    protected void assertJsonData(Async async, String address, Consumer<Object> asserter) {
        EventBusHelper.registerAssertReceivedData(vertx, async, address, asserter, closeClient());
    }

    protected WebSocket setupSockJsClient(TestContext context, Async async, Consumer<Throwable> error)
        throws InterruptedException {
        return setupSockJsClient(context, async, "/ws", null, error);
    }

    protected WebSocket setupSockJsClient(TestContext context, Async async, String path,
                                          Consumer<WebSocket> writerBeforeHandler, Consumer<Throwable> error)
        throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<WebSocket> wsReference = new AtomicReference<>();
        client.webSocket(wsOpt(requestOptions.setURI(Urls.combinePath(path, "websocket"))), ar -> {
            if (ar.succeeded()) {
                final WebSocket ws = ar.result();
                if (Objects.nonNull(writerBeforeHandler)) {
                    writerBeforeHandler.accept(ws);
                }
                wsReference.set(ws);
                latch.countDown();
                ws.endHandler(v -> testComplete(async, "CLIENT END"));
                ws.exceptionHandler(error::accept);
            } else {
                error.accept(ar.cause());
            }
        });
        context.assertTrue(latch.await(TestHelper.TEST_TIMEOUT_SEC, TimeUnit.SECONDS), "Timeout");
        return wsReference.get();
    }

    protected Consumer<WebSocket> clientRegister(String address) {
        return clientWrite(createWebsocketMsg(address, null, BridgeEventType.REGISTER));
    }

    protected Consumer<WebSocket> clientSend(String address, EventMessage body) {
        return clientWrite(createWebsocketMsg(address, body, BridgeEventType.SEND));
    }

    protected Consumer<WebSocket> clientWrite(JsonObject data) {
        return ws -> ws.writeFrame(WebSocketFrame.textFrame(data.encode(), true));
    }

    protected JsonObject createWebsocketMsg(String address, EventMessage body, BridgeEventType send) {
        return WebSocketEventMessage.builder().type(send).address(address).body(body).build().toJson();
    }

    protected WebSocketConnectOptions wsOpt(@NonNull RequestOptions opt) {
        //        HttpMethod method = opt.getMethod();
        //        .put("method", method.name())
        return new WebSocketConnectOptions(opt.setMethod(null).toJson());
    }

}

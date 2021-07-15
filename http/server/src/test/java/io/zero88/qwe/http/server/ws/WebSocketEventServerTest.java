package io.zero88.qwe.http.server.ws;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.zero88.utils.Urls;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.UpgradeRejectedException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.zero88.qwe.JsonHelper.Junit4;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.http.server.HttpServerPluginTestBase;
import io.zero88.qwe.http.server.HttpServerRouter;
import io.zero88.qwe.http.server.WebSocketTestHelper;
import io.zero88.qwe.http.server.mock.MockWebSocketEvent;
import io.zero88.qwe.http.server.mock.MockWebSocketEventListener;

@RunWith(VertxUnitRunner.class)
public class WebSocketEventServerTest extends HttpServerPluginTestBase implements WebSocketTestHelper {

    @Rule
    public Timeout timeout = Timeout.seconds(TestHelper.TEST_TIMEOUT_SEC);

    @Before
    public void before(TestContext context) throws IOException {
        super.before(context);
        this.httpConfig.getApiConfig().setEnabled(false);
        this.httpConfig.getWebSocketConfig().setEnabled(true);
        EventBusClient.create(createSharedData(vertx))
                      .register(MockWebSocketEvent.PROCESSOR.getAddress(), new MockWebSocketEventListener());
    }

    @Test
    public void test_greeting(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.GREETING));
        Async async = context.async(2);
        assertGreeting(context, async, "/ws/");
        assertGreeting(context, async, "/ws");
    }

    @Test
    public void test_not_found(TestContext context) {
        Async async = context.async(3);
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.GREETING));
        assertNotFound(context, async, "/socket1");
        assertNotFound(context, async, "/ws//");
        assertNotFound(context, async, "/ws/xyz");
    }

    @Test
    public void test_wsClient_send_invalid_json(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.NO_OUTBOUND));
        final Async async = context.async(1);
        final JsonObject expected = new JsonObject("{\"type\":\"err\",\"body\":\"invalid_json\"}");
        final Buffer req = Buffer.buffer("xx");
        setupWSClient(context, wsPath(MockWebSocketEvent.NO_OUTBOUND),
                      ws -> wsWrite(ws, req).handler(b -> Junit4.assertJson(context, async, expected, b)));
    }

    @Test
    public void test_wsClient_send_invalid_wsMessage(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.NO_OUTBOUND));
        final Async async = context.async(1);
        final JsonObject expected = EventMessage.error(EventAction.ACK, ErrorCode.INVALID_ARGUMENT,
                                                       "Invalid WebSocket message").toJson();
        JsonObject socketMsg = createWsMsg(MockWebSocketEvent.NO_OUTBOUND.inboundAddress(), null, BridgeEventType.SEND);
        JsonObject msg = socketMsg.put("body", new JsonObject("{\"type\":\"err\"}"));
        setupWSClient(context, wsPath(MockWebSocketEvent.NO_OUTBOUND),
                      ws -> wsWrite(ws, msg).handler(b -> Junit4.assertJson(context, async, expected, b)));
    }

    @Test
    public void test_wsClient_send_to_unknown_address(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.NO_OUTBOUND));
        final Async async = context.async(1);
        final JsonObject expected = new JsonObject("{\"type\":\"err\",\"body\":\"access_denied\"}");
        setupWSClient(context, wsPath(MockWebSocketEvent.NO_OUTBOUND),
                      ws -> this.wsSend(ws, "123", EventMessage.initial(EventAction.GET_ONE))
                                .handler(b -> Junit4.assertJson(context, async, expected, b)));
    }

    @Test
    public void test_wsClient_request_then_server_response(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.NO_OUTBOUND));
        final Async async = context.async(1);
        final JsonObject response = EventMessage.replySuccess(EventAction.GET_ONE, new JsonObject().put("data", "1"))
                                                .toJson();
        setupWSClient(context, wsPath(MockWebSocketEvent.NO_OUTBOUND),
                      ws -> this.wsSend(ws, MockWebSocketEvent.NO_OUTBOUND.inboundAddress(),
                                        EventMessage.initial(EventAction.GET_ONE))
                                .handler(b -> Junit4.assertJson(context, async, response, b)));
    }

    @Test
    public void test_wsClient_send_then_server_publish(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.FULL_PLAN));
        final Async async = context.async(2);
        final JsonObject outbound = EventMessage.success(EventAction.REPLY, EventAction.GET_LIST,
                                                         new JsonObject().put("data", Arrays.asList("1", "2", "3")))
                                                .toJson();
        final JsonObject ackExpected = EventMessage.success(EventAction.ACK).toJson();
        final String path = wsPath(MockWebSocketEvent.FULL_PLAN);
        setupWSClient(context, path, ws -> this.wsRegister(ws, MockWebSocketEvent.FULL_PLAN.outboundAddress())
                                               .handler(b -> Junit4.assertJson(context, async, outbound, b)));
        setupWSClient(context, path, ws -> this.wsSend(ws, MockWebSocketEvent.FULL_PLAN.inboundAddress(),
                                                       EventMessage.initial(EventAction.GET_LIST))
                                               .handler(b -> Junit4.assertJson(context, async, ackExpected, b)));
    }

    @Test
    public void test_wsClient_send_p2p_then_server_ack(TestContext context) {
    }

    @Test
    public void test_wsClient_request_then_server_error(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.NO_OUTBOUND));
        final Async async = context.async(1);
        final JsonObject expected = EventMessage.replyError(EventAction.DISCOVER,
                                                            ErrorMessage.parse(ErrorCode.INVALID_ARGUMENT, "Error"))
                                                .toJson();
        final EventMessage req = EventMessage.initial(EventAction.DISCOVER);
        setupWSClient(context, wsPath(MockWebSocketEvent.NO_OUTBOUND),
                      ws -> this.wsSend(ws, MockWebSocketEvent.NO_OUTBOUND.inboundAddress(), req)
                                .handler(b -> Junit4.assertJson(context, async, expected, b)));
    }

    @Test
    public void test_wsClient_request_unsupported_server_event(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.NO_OUTBOUND));
        final Async async = context.async(1);
        final JsonObject expected = EventMessage.replyError(EventAction.REMOVE,
                                                            ErrorMessage.parse(ErrorCode.SERVICE_NOT_FOUND,
                                                                               "Service not found | Cause: " +
                                                                               "Unsupported event [REMOVE] - Error " +
                                                                               "Code: UNSUPPORTED")).toJson();
        final EventMessage req = EventMessage.initial(EventAction.REMOVE);
        setupWSClient(context, wsPath(MockWebSocketEvent.NO_OUTBOUND),
                      ws -> this.wsSend(ws, MockWebSocketEvent.NO_OUTBOUND.inboundAddress(), req)
                                .handler(b -> Junit4.assertJson(context, async, expected, b)));
    }

    @Test
    public void test_wsClient_send_unsupported_server_event(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.FULL_PLAN));
        final Async async = context.async(2);
        final JsonObject ackExpected = EventMessage.replyError(EventAction.CREATE,
                                                               ErrorMessage.parse(ErrorCode.SERVICE_NOT_FOUND,
                                                                                  "Service not found | Cause: " +
                                                                                  "Unsupported event [CREATE] - Error" +
                                                                                  " Code: UNSUPPORTED")).toJson();
        final String path = wsPath(MockWebSocketEvent.FULL_PLAN);
        setupWSClient(context, path, ws -> this.wsRegister(ws, MockWebSocketEvent.FULL_PLAN.outboundAddress())
                                               .handler(b -> context.fail("Must not have data")));
        setupWSClient(context, path, ws -> this.wsSend(ws, MockWebSocketEvent.FULL_PLAN.inboundAddress(),
                                                       EventMessage.initial(EventAction.CREATE))
                                               .handler(b -> Junit4.assertJson(context, async, ackExpected, b)));
        TestHelper.LOGGER.info("SLEEP 2s to ensure no publish if error");
        TestHelper.sleep(2000);
        TestHelper.testComplete(async);
    }

    @Test
    public void test_wsServer_publish(TestContext context) {
        startServer(context, new HttpServerRouter().registerEventBusSocket(MockWebSocketEvent.ONLY_OUTBOUND));
        final Async async = context.async(4);
        final AtomicInteger c = new AtomicInteger();
        Supplier<JsonObject> expected = () -> EventMessage.initial(EventAction.parse("TICK"),
                                                                   new JsonObject().put("c", c.get())).toJson();
        Supplier<EventMessage> req = () -> EventMessage.initial(EventAction.parse("TICK"),
                                                                new JsonObject().put("c", c.incrementAndGet()));
        setupWSClient(context, wsPath(MockWebSocketEvent.ONLY_OUTBOUND),
                      ws -> this.wsRegister(ws, MockWebSocketEvent.ONLY_OUTBOUND.outboundAddress())
                                .handler(b -> Junit4.assertJson(context, async, expected.get(), b)));
        setupWSClient(context, wsPath(MockWebSocketEvent.ONLY_OUTBOUND),
                      ws -> this.wsRegister(ws, MockWebSocketEvent.ONLY_OUTBOUND.outboundAddress())
                                .handler(b -> Junit4.assertJson(context, async, expected.get(), b)));
        vertx().setPeriodic(1000, t -> EventBusClient.create(createSharedData(vertx()))
                                                     .fire(MockWebSocketEvent.ONLY_OUTBOUND.outboundAddress(),
                                                           MockWebSocketEvent.ONLY_OUTBOUND.outbound().getPattern(),
                                                           req.get()));
    }

    private void assertGreeting(TestContext context, Async async, String uri) {
        client().request(requestOptions().setURI(uri).setMethod(HttpMethod.GET), ar -> {
            if (!ar.succeeded()) {
                context.fail(ar.cause());
                return;
            }
            final HttpClientRequest request = ar.result();
            request.send(ar2 -> {
                if (!ar2.succeeded()) {
                    context.fail(ar2.cause());
                    return;
                }
                final HttpClientResponse resp = ar2.result();
                context.assertEquals(200, resp.statusCode());
                context.assertEquals("text/plain; charset=UTF-8", resp.getHeader("content-type"));
                resp.bodyHandler(buff -> {
                    context.assertEquals("Welcome to SockJS!\n", buff.toString());
                    testComplete(async);
                });
            });
        });
    }

    private void assertNotFound(TestContext context, Async async, String uri) {
        client().webSocket(wsOpt(requestOptions().setURI(uri)), ar -> {
            if (ar.succeeded()) {
                testComplete(async);
                return;
            }
            final Throwable cause = ar.cause();
            try {
                if (cause instanceof UpgradeRejectedException) {
                    context.assertEquals(404, ((UpgradeRejectedException) cause).getStatus());
                }
            } finally {
                testComplete(async);
            }
        });
    }

    private String wsPath(WebSocketServerPlan fullPlan) {
        return Urls.combinePath(httpConfig.getWebSocketConfig().getPath(), fullPlan.getPath());
    }

}

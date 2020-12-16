package io.github.zero88.msa.bp.http.client;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.zero88.msa.bp.TestHelper;
import io.github.zero88.msa.bp.TestHelper.JsonHelper;
import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventContractor;
import io.github.zero88.msa.bp.event.EventListener;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.event.EventPattern;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.msa.bp.exceptions.HttpException;
import io.github.zero88.msa.bp.exceptions.NotFoundException;
import io.github.zero88.msa.bp.http.HostInfo;
import io.github.zero88.msa.bp.http.event.WebSocketClientEventMetadata;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RunWith(VertxUnitRunner.class)
public class WebSocketClientDelegateTest {

    private static final EventModel LISTENER = EventModel.builder()
                                                         .address("ws.listener")
                                                         .local(true)
                                                         .pattern(EventPattern.POINT_2_POINT)
                                                         .addEvents(EventAction.UNKNOWN)
                                                         .build();
    private static final String PUBLISHER_ADDRESS = "ws.publisher";

    @Rule
    public Timeout timeout = Timeout.seconds(TestHelper.TEST_TIMEOUT_SEC * 2);
    private Vertx vertx;
    private HttpClientConfig config;
    private HostInfo hostInfo;

    @BeforeClass
    public static void beforeClass() {
        TestHelper.setup();
    }

    @Before
    public void setup() {
        vertx = Vertx.vertx();
        config = new HttpClientConfig();
        hostInfo = HostInfo.builder().host("echo.websocket.org").port(443).ssl(true).build();
    }

    @After
    public void teardown(TestContext context) {
        HttpClientRegistry.getInstance().clear();
        vertx.close(context.asyncAssertSuccess());
    }

    @Test(expected = HttpException.class)
    public void test_connect_failed_due_unknown_dns() {
        config.getOptions().setConnectTimeout(6 * 1000);
        HostInfo opt = HostInfo.builder().host("echo.websocket.test").port(443).ssl(true).build();
        WebSocketClientDelegate client = WebSocketClientDelegate.create(vertx, config, opt);
        client.open(WebSocketClientEventMetadata.create("/echo", LISTENER, PUBLISHER_ADDRESS), null);
    }

    @Test
    public void test_connect_and_send(TestContext context) throws InterruptedException {
        Async async = context.async();
        WebSocketClientDelegate client = WebSocketClientDelegate.create(vertx, config, hostInfo);
        client.getEventClient()
              .register(LISTENER, new EventAsserter(LISTENER, context, async, new JsonObject().put("k", 1)));
        client.open(WebSocketClientEventMetadata.create("/echo", LISTENER, PUBLISHER_ADDRESS), null);
        Thread.sleep(1000);
        client.getEventClient()
              .publish(PUBLISHER_ADDRESS, EventMessage.initial(EventAction.SEND, new JsonObject().put("k", 1)));
    }

    @Test(expected = HttpException.class)
    public void test_not_found(TestContext context) {
        WebSocketClientDelegate client = WebSocketClientDelegate.create(vertx, config, hostInfo);
        try {
            client.open(WebSocketClientEventMetadata.create("/xxx", LISTENER, PUBLISHER_ADDRESS), null);
        } catch (HttpException ex) {
            context.assertEquals(HttpResponseStatus.NOT_FOUND, ex.getStatusCode());
            context.assertEquals(ErrorCode.NOT_FOUND, ((BlueprintException) ex.getCause()).errorCode());
            throw ex;
        }
    }

    @Test
    public void test_cache(TestContext context) throws InterruptedException {
        context.assertTrue(HttpClientRegistry.getInstance().getWsRegistries().isEmpty());

        final WebSocketClientDelegate client1 = WebSocketClientDelegate.create(vertx, config, hostInfo);
        context.assertEquals(1, HttpClientRegistry.getInstance().getWsRegistries().size());
        context.assertEquals(1, HttpClientRegistry.getInstance().getWsRegistries().get(hostInfo).current());

        final WebSocketClientDelegate client2 = WebSocketClientDelegate.create(vertx, config, hostInfo);
        context.assertEquals(1, HttpClientRegistry.getInstance().getWsRegistries().size());
        context.assertEquals(2, HttpClientRegistry.getInstance().getWsRegistries().get(hostInfo).current());

        final HostInfo host2 = HostInfo.builder().host("echo.websocket.google").build();
        final WebSocketClientDelegate client3 = WebSocketClientDelegate.create(vertx, config, host2);
        context.assertEquals(2, HttpClientRegistry.getInstance().getWsRegistries().size());
        context.assertEquals(1, HttpClientRegistry.getInstance().getWsRegistries().get(host2).current());

        client1.open(WebSocketClientEventMetadata.create("/echo", LISTENER, PUBLISHER_ADDRESS), null);
        client2.open(WebSocketClientEventMetadata.create("/echo", LISTENER, PUBLISHER_ADDRESS), null);
        try {
            client3.open(WebSocketClientEventMetadata.create("/echo", LISTENER, PUBLISHER_ADDRESS), null);
        } catch (HttpException e) {
            context.assertEquals(1, HttpClientRegistry.getInstance().getWsRegistries().size());
        }
        client1.close();
        client2.close();

        Thread.sleep(1000);
        context.assertTrue(HttpClientRegistry.getInstance().getWsRegistries().isEmpty());
    }

    @RequiredArgsConstructor
    static class EventAsserter implements EventListener {

        private final EventModel eventModel;
        private final TestContext context;
        private final Async async;
        private final JsonObject expected;

        @EventContractor(action = "UNKNOWN", returnType = int.class)
        public int send(JsonObject data) {
            JsonHelper.assertJson(context, async, expected, data);
            return 1;
        }

        @Override
        public @NonNull Collection<EventAction> getAvailableEvents() {
            return new ArrayList<>(eventModel.getEvents());
        }

    }

}

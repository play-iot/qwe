package io.zero88.qwe.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.mock.MockEventListener;
import io.zero88.qwe.event.mock.MockEventListener.MockEventFailed;
import io.zero88.qwe.event.mock.MockEventListener.MockFuture;
import io.zero88.qwe.event.mock.MockEventListener.MockReceiveSendOrPublish;
import io.zero88.qwe.event.mock.MockEventListener.MockWithContext;
import io.zero88.qwe.event.mock.MockEventListener.MockWithVariousParams;
import io.zero88.qwe.exceptions.ErrorCode;

@ExtendWith(VertxExtension.class)
public class EventListenerTest {

    EventBusClient eventBusClient;
    private String address;

    @BeforeEach
    void setup(Vertx vertx) {
        address = "test.request";
        eventBusClient = EventBusClient.create(SharedDataLocalProxy.create(vertx, EventListenerTest.class.getName()));
    }

    @Test
    void test_request_error(VertxTestContext testContext) {
        final EventAction err = EventAction.parse("ERR");
        eventBusClient.register(address, new MockEventFailed());
        eventBusClient.request(address, EventMessage.initial(err)).onSuccess(msg -> testContext.verify(() -> {
            Assertions.assertEquals(EventAction.REPLY, msg.getAction());
            Assertions.assertEquals(err, msg.getPrevAction());
            Assertions.assertTrue(msg.isError());
            Assertions.assertEquals(ErrorCode.INVALID_ARGUMENT, msg.getError().getCode());
            testContext.completeNow();
        }));
    }

    @Test
    void test_request_success(VertxTestContext testContext) {
        eventBusClient.register(address, new MockWithVariousParams());
        eventBusClient.request(address, EventMessage.initial(EventAction.GET_ONE, new JsonObject().put("id", "123")))
                      .onSuccess(msg -> testContext.verify(() -> {
                          Assertions.assertEquals(EventAction.REPLY, msg.getAction());
                          Assertions.assertEquals(EventAction.GET_ONE, msg.getPrevAction());
                          Assertions.assertEquals(new JsonObject().put("data", 123), msg.getData());
                          testContext.completeNow();
                      }));
    }

    @Test
    void test_request_then_void_resp(VertxTestContext testContext) {
        eventBusClient.register(address, new MockWithVariousParams());
        eventBusClient.request(address, EventMessage.initial(EventAction.NOTIFY, new JsonObject().put("id", "123")))
                      .onSuccess(msg -> testContext.verify(() -> {
                          Assertions.assertEquals(EventAction.REPLY, msg.getAction());
                          Assertions.assertEquals(EventAction.NOTIFY, msg.getPrevAction());
                          Assertions.assertNull(msg.getData());
                          testContext.completeNow();
                      }));
    }

    @Test
    void test_request_then_async_resp(VertxTestContext testContext) {
        eventBusClient.register(address, new MockFuture());
        eventBusClient.request(address, EventMessage.initial(EventAction.GET_ONE, new JsonObject().put("id", 111)))
                      .onSuccess(msg -> testContext.verify(() -> {
                          Assertions.assertEquals(EventAction.REPLY, msg.getAction());
                          Assertions.assertEquals(new JsonObject().put("resp", 111), msg.getData());
                          testContext.completeNow();
                      }));
    }

    @Test
    void test_request_then_async_void_resp(VertxTestContext testContext) {
        eventBusClient.register(address, new MockFuture());
        eventBusClient.request(address, EventMessage.initial(EventAction.CREATE, new JsonObject().put("id", 123)))
                      .onSuccess(msg -> testContext.verify(() -> {
                          Assertions.assertEquals(EventAction.REPLY, msg.getAction());
                          Assertions.assertTrue(msg.isSuccess());
                          Assertions.assertNull(msg.getData());
                          testContext.completeNow();
                      }));
    }

    @Test
    void test_request_then_failed_future(VertxTestContext testContext) {
        final EventAction errorEvent = MockEventListener.ERROR_EVENT;
        eventBusClient.register(address, new MockFuture());
        eventBusClient.request(address, EventMessage.initial(errorEvent, new JsonObject().put("id", 1)))
                      .onSuccess(msg -> testContext.verify(() -> {
                          Assertions.assertEquals(EventAction.REPLY, msg.getAction());
                          Assertions.assertEquals(errorEvent, msg.getPrevAction());
                          Assertions.assertTrue(msg.isError());
                          Assertions.assertEquals(ErrorCode.TIMEOUT_ERROR, msg.getError().getCode());
                          testContext.completeNow();
                      }));
    }

    @Test
    void test_request_unsupported_action(VertxTestContext testContext) {
        final EventAction any = EventAction.parse("ANY");
        eventBusClient.register(address, new MockFuture());
        eventBusClient.request(address, EventMessage.initial(any, new JsonObject().put("id", 1)))
                      .onSuccess(msg -> testContext.verify(() -> {
                          Assertions.assertEquals(EventAction.REPLY, msg.getAction());
                          Assertions.assertEquals(any, msg.getPrevAction());
                          Assertions.assertTrue(msg.isError());
                          Assertions.assertEquals(ErrorCode.SERVICE_NOT_FOUND, msg.getError().getCode());
                          Assertions.assertEquals(
                              "Service not found | Cause: Unsupported event [ANY] - Error Code: UNSUPPORTED | Cause: " +
                              "Unsupported event [ANY] - Error Code: UNSUPPORTED", msg.getError().getMessage());
                          testContext.completeNow();
                      }));
    }

    @Test
    void test_request_with_context_then_async_resp(VertxTestContext testContext) {
        final JsonObject req = new JsonObject().put("tik", 123);
        eventBusClient.register(address, new MockWithContext());
        eventBusClient.request(address, EventMessage.initial(EventAction.parse("EB"), req))
                      .onSuccess(msg -> testContext.verify(() -> {
                          Assertions.assertEquals(EventAction.REPLY, msg.getAction());
                          Assertions.assertTrue(msg.isSuccess());
                          Assertions.assertEquals(new JsonObject().put("received", req), msg.getData());
                          testContext.completeNow();
                      }));
    }

    @Test
    void test_request_with_context_then_use_future_then_async_resp(VertxTestContext testContext) {
        final JsonObject req = new JsonObject().put("tik", 123);
        eventBusClient.register(address, new MockWithContext());
        eventBusClient.request(address, EventMessage.initial(EventAction.parse("INVOKE"), req))
                      .onSuccess(msg -> testContext.verify(() -> {
                          System.out.println(msg.toJson());
                          Assertions.assertEquals(EventAction.REPLY, msg.getAction());
                          Assertions.assertTrue(msg.isSuccess());
                          Assertions.assertEquals(new JsonObject().put("received", req), msg.getData());
                          testContext.completeNow();
                      }));
    }

    @Test
    void test_send(VertxTestContext testContext) {
        final String address = "test.send";
        final Checkpoint checkpoint = testContext.checkpoint();
        eventBusClient.register(address, new MockReceiveSendOrPublish("id1", checkpoint));
        eventBusClient.send(address, EventMessage.initial(EventAction.GET_ONE, new JsonObject().put("id", 111)));
    }

    @Test
    void test_publish(VertxTestContext testContext) {
        final String address = "test.publish";
        final Checkpoint checkpoint = testContext.checkpoint(2);
        eventBusClient.register(address, new MockReceiveSendOrPublish("id1", checkpoint));
        eventBusClient.register(address, new MockReceiveSendOrPublish("id2", checkpoint));
        eventBusClient.publish(address, EventMessage.initial(EventAction.GET_ONE, new JsonObject().put("id", 111)));
    }

}

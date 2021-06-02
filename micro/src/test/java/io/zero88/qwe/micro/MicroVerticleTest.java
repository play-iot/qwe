package io.zero88.qwe.micro;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.EventBusHelper;
import io.zero88.qwe.JsonHelper;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.micro.filter.ServiceLocatorParams;
import io.zero88.qwe.micro.filter.ServiceScope;
import io.zero88.qwe.micro.mock.MockEventBusService;
import io.zero88.qwe.micro.servicetype.EventMessageHttpService;

@RunWith(VertxUnitRunner.class)
public class MicroVerticleTest extends BaseMicroVerticleTest {

    @Test
    public void test_serviceDiscovery_register_eventbus(TestContext context) {
        final Async async = context.async(4);
        JsonObject expected = new JsonObject("{\"location\":{\"endpoint\":\"address1\"},\"metadata\":{\"service" +
                                             ".interface\":\"io.zero88.qwe.micro.mock.MockEventBusService\"}," +
                                             "\"name\":\"test\",\"status\":\"UP\"," +
                                             "\"type\":\"eventbus-service-proxy\"}");
        EventBusHelper.registerAssertReceivedData(ebClient.getVertx(), async,
                                                  config.getDiscoveryConfig().getAnnounceAddress(),
                                                  JsonHelper.asserter(context, async, expected));
        micro.getDiscovery()
             .addRecord(EventBusService.createRecord("test", "address1", MockEventBusService.class))
             .map(record -> RequestData.builder()
                                       .body(new JsonObject().put(ServiceLocatorParams.IDENTIFIER,
                                                                  record.getRegistration()))
                                       .filter(new JsonObject().put(ServiceLocatorParams.SCOPE, ServiceScope.ALL))
                                       .build())
             .map(req -> EventMessage.initial(EventAction.GET_ONE, req))
             .onFailure(context::fail)
             .onSuccess(msg -> {
                 TestHelper.testComplete(async);
                 ebClient.request(config.getGatewayConfig().getIndexAddress(), msg).onSuccess(reply -> {
                     JsonObject indexExpected = new JsonObject(
                         "{\"status\":\"SUCCESS\",\"action\":\"REPLY\",\"prevAction\":\"GET_ONE\"," +
                         "\"data\":{\"name\":\"test\",\"endpoint\":\"address1\",\"type\":\"eventbus-service-proxy\"," +
                         "\"status\":\"UP\"}}");
                     JsonHelper.assertJson(context, async, indexExpected, reply.toJson());
                 });
             });
    }

    @Test
    public void test_serviceDiscovery_register_http(TestContext context) {
        final Async async = context.async(4);
        JsonObject expected = new JsonObject("{\"location\":{\"endpoint\":\"http://123.456.0.1:1234/xyz\"," +
                                             "\"host\":\"123.456.0.1\",\"port\":1234,\"root\":\"/xyz\"," +
                                             "\"ssl\":false},\"metadata\":{\"meta\":\"test\"},\"name\":\"http.test\"," +
                                             "\"status\":\"UP\",\"type\":\"http-endpoint\"}");
        EventBusHelper.registerAssertReceivedData(ebClient.getVertx(), async,
                                                  config.getDiscoveryConfig().getAnnounceAddress(),
                                                  JsonHelper.asserter(context, async, expected));
        micro.getDiscovery()
             .addRecord("http.test", new HttpLocation().setHost("123.456.0.1").setPort(1234).setRoot("/xyz"),
                        new JsonObject().put("meta", "test"))
             .map(record -> RequestData.builder()
                                       .body(new JsonObject().put(ServiceLocatorParams.IDENTIFIER,
                                                                  record.getRegistration()))
                                       .build())
             .map(req -> EventMessage.initial(EventAction.GET_ONE, req))
             .onFailure(context::fail)
             .onSuccess(msg -> {
                 TestHelper.testComplete(async);
                 ebClient.request(config.getGatewayConfig().getIndexAddress(), msg).onSuccess(reply -> {
                     JsonObject indexExpected = new JsonObject(
                         "{\"status\":\"SUCCESS\",\"action\":\"REPLY\",\"prevAction\":\"GET_ONE\"," +
                         "\"data\":{\"name\":\"http.test\",\"status\":\"UP\",\"type\":\"http-endpoint\"," +
                         "\"endpoint\":\"http://123.456.0.1:1234/xyz\"}}");
                     JsonHelper.assertJson(context, async, indexExpected, reply.toJson());
                 });
             });
    }

    @Test
    public void test_serviceDiscovery_register_eventMessage(TestContext context) {
        final Async async = context.async(4);
        JsonObject expected = new JsonObject(
            "{\"location\":{\"servicePath\":\"/abc\",\"useRequestData\":true,\"mapping\":[{\"action\":\"CREATE\"," +
            "\"capturePath\":\"/abc\",\"regexPath\":\"/abc\",\"method\":\"POST\"},{\"action\":\"UPDATE\"," +
            "\"capturePath\":\"/abc/:ka\",\"regexPath\":\"/abc/.+\",\"method\":\"PUT\"},{\"action\":\"PATCH\"," +
            "\"capturePath\":\"/abc/:ka\",\"regexPath\":\"/abc/.+\",\"method\":\"PATCH\"},{\"action\":\"GET_LIST\"," +
            "\"capturePath\":\"/abc\",\"regexPath\":\"/abc\",\"method\":\"GET\"},{\"action\":\"GET_ONE\"," +
            "\"capturePath\":\"/abc/:ka\",\"regexPath\":\"/abc/.+\",\"method\":\"GET\"},{\"action\":\"REMOVE\"," +
            "\"capturePath\":\"/abc/:ka\",\"regexPath\":\"/abc/.+\",\"method\":\"DELETE\"}],\"endpoint\":\"address" +
            ".1\"},\"metadata\":{},\"name\":\"event-message\",\"status\":\"UP\",\"type\":\"eventbus-message-http\"}");
        EventBusHelper.registerAssertReceivedData(ebClient.getVertx(), async,
                                                  config.getDiscoveryConfig().getAnnounceAddress(),
                                                  JsonHelper.asserter(context, async, expected,
                                                                      JSONCompareMode.LENIENT));
        micro.getDiscovery()
             .addRecord(EventMessageHttpService.createRecord("event-message", "address.1",
                                                             EventMethodDefinition.createDefault("/abc", "/:ka")))
             .map(record -> RequestData.builder()
                                       .body(new JsonObject().put(ServiceLocatorParams.IDENTIFIER,
                                                                  record.getRegistration()))
                                       .build())
             .map(req -> EventMessage.initial(EventAction.GET_ONE, req))
             .onFailure(context::fail)
             .onSuccess(msg -> {
                 TestHelper.testComplete(async);
                 ebClient.request(config.getGatewayConfig().getIndexAddress(), msg).onSuccess(reply -> {
                     JsonObject indexExpected = new JsonObject(
                         "{\"status\":\"SUCCESS\",\"action\":\"REPLY\",\"prevAction\":\"GET_ONE\"," +
                         "\"data\":{\"endpoint\":\"address.1\",\"paths\":[{\"method\":\"POST\",\"path\":\"/abc\"}," +
                         "{\"method\":\"PUT\",\"path\":\"/abc/:ka\"},{\"method\":\"PATCH\",\"path\":\"/abc/:ka\"}," +
                         "{\"method\":\"GET\",\"path\":\"/abc\"},{\"method\":\"GET\",\"path\":\"/abc/:ka\"}," +
                         "{\"method\":\"DELETE\",\"path\":\"/abc/:ka\"}],\"name\":\"event-message\",\"status\":\"UP\"}}");
                     JsonHelper.assertJson(context, async, indexExpected, reply.toJson());
                 });
             });
    }

}

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
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.micro.filter.ServiceLocatorParams;
import io.zero88.qwe.micro.mock.MockEventBusService;
import io.zero88.qwe.micro.servicetype.EventMessageService;
import io.zero88.qwe.micro.type.ServiceScope;

@RunWith(VertxUnitRunner.class)
public class MicroVerticleTest extends BaseMicroVerticleTest {

    @Test
    public void test_serviceDiscovery_local_register_eventbus(TestContext context) {
        final Async async = context.async(2);
        JsonObject expected = new JsonObject("{\"location\":{\"endpoint\":\"address1\"},\"metadata\":{\"service" +
                                             ".interface\":\"io.zero88.qwe.micro.mock.MockEventBusService\"}," +
                                             "\"name\":\"test\",\"status\":\"UP\"," +
                                             "\"type\":\"eventbus-service-proxy\"}");
        EventBusHelper.registerAssertReceivedData(ebClient.getVertx(), async,
                                                  config.getDiscoveryConfig().getAnnounceAddress(),
                                                  JsonHelper.asserter(context, async, expected));
        micro.getDiscovery()
             .addRecord(EventBusService.createRecord("test", "address1", MockEventBusService.class))
             .onSuccess(record -> {
                 JsonObject indexExpected = new JsonObject(
                     "{\"status\":\"SUCCESS\",\"action\":\"REPLY\",\"prevAction\":\"GET_LIST\"," +
                     "\"data\":{\"apis\":[{\"name\":\"test\",\"type\":\"eventbus-service-proxy\",\"status\":\"UP\"," +
                     "\"location\":\"address1\"}]}}");
                 RequestData payload = RequestData.builder()
                                                  .filter(new JsonObject().put(ServiceLocatorParams.SCOPE,
                                                                               ServiceScope.INTERNAL))
                                                  .build();
                 ebClient.request(config.getGatewayConfig().getIndexAddress(),
                                  EventMessage.initial(EventAction.GET_LIST, payload))
                         .onSuccess(msg -> JsonHelper.assertJson(context, async, indexExpected, msg.toJson(),
                                                                 JSONCompareMode.LENIENT));
             });
    }

    @Test
    public void test_serviceDiscovery_local_register_http(TestContext context) {
        final Async async = context.async(2);
        JsonObject expected = new JsonObject("{\"location\":{\"endpoint\":\"http://123.456.0.1:1234/api\"," +
                                             "\"host\":\"123.456.0.1\",\"port\":1234,\"root\":\"/api\"," +
                                             "\"ssl\":false},\"metadata\":{\"meta\":\"test\"},\"name\":\"http.test\"," +
                                             "\"status\":\"UP\",\"type\":\"http-endpoint\"}");
        EventBusHelper.registerAssertReceivedData(ebClient.getVertx(), async,
                                                  config.getDiscoveryConfig().getAnnounceAddress(),
                                                  JsonHelper.asserter(context, async, expected));
        micro.getDiscovery()
             .addRecord("http.test", new HttpLocation().setHost("123.456.0.1").setPort(1234).setRoot("/api"),
                        new JsonObject().put("meta", "test"))
             .onSuccess(record -> {
                 final JsonObject indexExpected = new JsonObject(
                     "{\"status\":\"SUCCESS\",\"action\":\"GET_LIST\",\"data\":{\"apis\":[{\"name\":\"http.test\"," +
                     "\"status\":\"UP\",\"type\":\"http-endpoint\",\"location\":\"http://123.456.0.1:1234/api\"}]}}");
                 ebClient.request(config.getGatewayConfig().getIndexAddress(),
                                  EventMessage.initial(EventAction.GET_LIST))
                         .onSuccess(msg -> JsonHelper.assertJson(context, async, indexExpected, msg.toJson(),
                                                                 JSONCompareMode.LENIENT));
             });
    }

    @Test
    public void test_serviceDiscovery_local_register_eventMessage(TestContext context) {
        final Async async = context.async(2);
        JsonObject expected = new JsonObject(
            "{\"location\":{\"endpoint\":\"address.1\"},\"metadata\":{\"eventMethods\":{\"servicePath\":\"/path\"," +
            "\"mapping\":[{\"action\":\"GET_LIST\",\"method\":\"GET\",\"capturePath\":\"/path\"," +
            "\"regexPath\":\"/path\"},{\"action\":\"UPDATE\",\"method\":\"PUT\",\"capturePath\":\"/path/:param\"," +
            "\"regexPath\":\"/path/.+\"},{\"action\":\"CREATE\",\"method\":\"POST\",\"capturePath\":\"/path\"," +
            "\"regexPath\":\"/path\"},{\"action\":\"PATCH\",\"method\":\"PATCH\",\"capturePath\":\"/path/:param\"," +
            "\"regexPath\":\"/path/.+\"},{\"action\":\"GET_ONE\",\"method\":\"GET\",\"capturePath\":\"/path/:param\"," +
            "\"regexPath\":\"/path/.+\"},{\"action\":\"REMOVE\",\"method\":\"DELETE\"," +
            "\"capturePath\":\"/path/:param\",\"regexPath\":\"/path/.+\"}],\"useRequestData\":true}}," +
            "\"name\":\"event-message\",\"status\":\"UP\",\"type\":\"eventbus-message-service\"}");
        EventBusHelper.registerAssertReceivedData(ebClient.getVertx(), async,
                                                  config.getDiscoveryConfig().getAnnounceAddress(),
                                                  JsonHelper.asserter(context, async, expected,
                                                                      JSONCompareMode.LENIENT));
        micro.getDiscovery()
             .addRecord(EventMessageService.createRecord("event-message", "address.1",
                                                         EventMethodDefinition.createDefault("/path", "/:param")))
             .onSuccess(record -> {
                 final JsonObject indexExpected = new JsonObject(
                     "{\"status\":\"SUCCESS\",\"action\":\"REPLY\",\"prevAction\":\"GET_LIST\",\"data\":{\"apis\":" +
                     "[{\"name\":\"event-message\",\"status\":\"UP\",\"location\":\"address.1\"," +
                     "\"endpoints\":[{\"method\":\"GET\",\"path\":\"/path\"},{\"method\":\"PATCH\"," +
                     "\"path\":\"/path/:param\"},{\"method\":\"PUT\",\"path\":\"/path/:param\"},{\"method\":\"POST\"," +
                     "\"path\":\"/path\"},{\"method\":\"DELETE\",\"path\":\"/path/:param\"},{\"method\":\"GET\"," +
                     "\"path\":\"/path/:param\"}]}]}}");
                 ebClient.request(config.getGatewayConfig().getIndexAddress(),
                                  EventMessage.initial(EventAction.GET_LIST))
                         .onSuccess(msg -> JsonHelper.assertJson(context, async, indexExpected, msg.toJson(),
                                                                 JSONCompareMode.LENIENT));
             });
    }

}

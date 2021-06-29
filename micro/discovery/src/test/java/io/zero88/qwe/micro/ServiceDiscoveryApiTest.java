package io.zero88.qwe.micro;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;

import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.JsonHelper;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.micro.filter.ServiceFilterParam;
import io.zero88.qwe.micro.mock.MockEventBusService;

public class ServiceDiscoveryApiTest extends BaseMicroVerticleTest {

    @Test
    public void test_serviceDiscovery_register_eventbus_service(VertxTestContext context) {
        final JsonObject expected = new JsonObject("{\"location\":{\"endpoint\":\"address1\"},\"metadata\":{\"service" +
                                                   ".interface\":\"io.zero88.qwe.micro.mock.MockEventBusService\"}," +
                                                   "\"name\":\"test\",\"status\":\"UP\"," +
                                                   "\"type\":\"eventbus-service-proxy\"}");
        final Record rec = EventBusService.createRecord("test", "address1", MockEventBusService.class);
        registerThenAssert(context, expected, rec);
    }

    @Test
    public void test_serviceDiscovery_register_http(VertxTestContext context) {
        final JsonObject expected = new JsonObject("{\"location\":{\"endpoint\":\"http://123.456.0.1:1234/xyz\"," +
                                                   "\"host\":\"123.456.0.1\",\"port\":1234,\"root\":\"/xyz\"," +
                                                   "\"ssl\":false},\"metadata\":{\"meta\":\"test\"},\"name\":\"http" +
                                                   ".test\"," + "\"status\":\"UP\",\"type\":\"http-endpoint\"}");
        final Record rec = RecordHelper.create("http.test",
                                               new HttpLocation().setHost("123.456.0.1").setPort(1234).setRoot("/xyz"),
                                               new JsonObject().put("meta", "test"));
        registerThenAssert(context, expected, rec);
    }

    @Test
    public void test_serviceDiscovery_register_eventMessage(VertxTestContext context) {
        final JsonObject expected = new JsonObject(
            "{\"location\":{\"servicePath\":\"/abc\",\"useRequestData\":true,\"mapping\":[{\"action\":\"CREATE\"," +
            "\"capturePath\":\"/abc\",\"regexPath\":\"/abc\",\"method\":\"POST\"},{\"action\":\"UPDATE\"," +
            "\"capturePath\":\"/abc/:ka\",\"regexPath\":\"/abc/.+\",\"method\":\"PUT\"},{\"action\":\"PATCH\"," +
            "\"capturePath\":\"/abc/:ka\",\"regexPath\":\"/abc/.+\",\"method\":\"PATCH\"},{\"action\":\"GET_LIST\"," +
            "\"capturePath\":\"/abc\",\"regexPath\":\"/abc\",\"method\":\"GET\"},{\"action\":\"GET_ONE\"," +
            "\"capturePath\":\"/abc/:ka\",\"regexPath\":\"/abc/.+\",\"method\":\"GET\"},{\"action\":\"REMOVE\"," +
            "\"capturePath\":\"/abc/:ka\",\"regexPath\":\"/abc/.+\",\"method\":\"DELETE\"}],\"endpoint\":\"address" +
            ".1\"},\"metadata\":{},\"name\":\"event-message\",\"status\":\"UP\",\"type\":\"eventbus-message-http\"}");
        final Record rec = RecordHelper.create("event-message", "address.1",
                                               EventMethodDefinition.createDefault("/abc", "/:ka"));
        registerThenAssert(context, expected, rec);
    }

    private void registerThenAssert(VertxTestContext context, JsonObject expected, Record rec) {
        final ServiceDiscoveryApi dis = microContext.getDiscovery();
        final Checkpoint checkpoint = context.checkpoint();
        dis.register(rec)
           .flatMap(r1 -> dis.findOne(new RequestFilter().put(ServiceFilterParam.IDENTIFIER, r1.getRegistration()))
                             .onSuccess(r2 -> context.verify(() -> {
                                 Assertions.assertEquals(r2, r1);
                                 JsonHelper.assertJson(expected, r1.toJson(),
                                                       Customization.customization("registration", (o1, o2) -> true));
                             })))
           .onSuccess(msg -> checkpoint.flag())
           .onFailure(context::failNow);
    }

}

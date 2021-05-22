package io.zero88.qwe.micro;

import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.EventBusHelper;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.JsonHelper;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.micro.filter.ServiceLocatorParams;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.micro.mock.MockEventbusService;
import io.zero88.qwe.micro.servicetype.EventMessageService;
import io.zero88.qwe.micro.type.ServiceScope;

@RunWith(VertxUnitRunner.class)
public class MicroContextTest {

    private Vertx vertx;
    private MicroContext micro;

    @BeforeClass
    public static void init() {
        TestHelper.setup();
    }

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
    }

    @After
    public void tearDown() {
        if (Objects.nonNull(micro)) {
            micro.unregister();
        }
        vertx.close();
    }

    @Test(expected = NullPointerException.class)
    public void test_not_enable_serviceDiscovery_cluster() {
        new MicroContext().setup(vertx, IConfig.fromClasspath("micro.json", MicroConfig.class))
                          .getClusterInvoker()
                          .get();
    }

    @Test(expected = NullPointerException.class)
    public void test_not_enable_serviceDiscovery_local() {
        new MicroContext().setup(vertx, IConfig.fromClasspath("micro.json", MicroConfig.class)).getLocalInvoker().get();
    }

    @Test(expected = NullPointerException.class)
    public void test_not_enable_circuitBreaker() {
        new MicroContext().setup(vertx, IConfig.fromClasspath("micro.json", MicroConfig.class))
                          .getBreakerInvoker()
                          .get();
    }

    @Test
    public void test_enable_serviceDiscovery_local_and_circuitBreaker() {
        MicroContext context = new MicroContext().setup(vertx, IConfig.fromClasspath("local.json", MicroConfig.class));
        context.getLocalInvoker().get();
        context.getBreakerInvoker().get();
    }

    @Test
    public void test_serviceDiscovery_local_register_eventbus(TestContext context) {
        final Async async = context.async(2);
        final MicroConfig config = IConfig.fromClasspath("local.json", MicroConfig.class);
        micro = new MicroContext().setup(vertx, IConfig.fromClasspath("local.json", MicroConfig.class));
        JsonObject expected = new JsonObject("{\"location\":{\"endpoint\":\"address1\"},\"metadata\":{\"service" +
                                             ".interface\":\"io.zero88.qwe.micro.mock" + ".MockEventbusService\"}," +
                                             "\"name\":\"test\",\"status\":\"UP\"," +
                                             "\"type\":\"eventbus-service-proxy\"}");
        EventBusHelper.assertReceivedData(vertx, async, micro.getLocalInvoker().getConfig().getAnnounceAddress(),
                                          JsonHelper.asserter(context, async, expected));
        EventBusClient client = EventBusClient.create(SharedDataLocalProxy.create(vertx, MicroContext.class.getName()));
        micro.getLocalInvoker()
             .addRecord(EventBusService.createRecord("test", "address1", MockEventbusService.class))
             .onSuccess(record -> {
                 final JsonObject indexExpected = new JsonObject(
                     "{\"status\":\"SUCCESS\",\"action\":\"GET_LIST\",\"data\":{\"apis\":[{\"name\":\"test\"," +
                     "\"type\":\"eventbus-service-proxy\",\"status\":\"UP\",\"location\":\"address1\"}]}}");
                 final RequestData payload = RequestData.builder()
                                                        .filter(new JsonObject().put(ServiceLocatorParams.SCOPE,
                                                                                     ServiceScope.INTERNAL))
                                                        .build();
                 client.request(config.getGatewayConfig().getIndexAddress(),
                                EventMessage.initial(EventAction.GET_LIST, payload))
                       .onSuccess(msg -> JsonHelper.assertJson(context, async, indexExpected, msg.toJson(),
                                                               JSONCompareMode.LENIENT));
             });
    }

    @Test
    public void test_serviceDiscovery_local_register_http(TestContext context) {
        final Async async = context.async(2);
        final MicroConfig config = IConfig.fromClasspath("local.json", MicroConfig.class);
        micro = new MicroContext().setup(vertx, config);
        JsonObject expected = new JsonObject("{\"location\":{\"endpoint\":\"http://123.456.0.1:1234/api\"," +
                                             "\"host\":\"123.456.0.1\",\"port\":1234,\"root\":\"/api\"," +
                                             "\"ssl\":false},\"metadata\":{\"meta\":\"test\"},\"name\":\"http.test\"," +
                                             "\"status\":\"UP\",\"type\":\"http-endpoint\"}");
        EventBusHelper.assertReceivedData(vertx, async, micro.getLocalInvoker().getConfig().getAnnounceAddress(),
                                          JsonHelper.asserter(context, async, expected));
        EventBusClient client = EventBusClient.create(SharedDataLocalProxy.create(vertx, MicroContext.class.getName()));
        micro.getLocalInvoker()
             .addHttpRecord("http.test", new HttpLocation().setHost("123.456.0.1").setPort(1234).setRoot("/api"),
                            new JsonObject().put("meta", "test"))
             .onSuccess(record -> {
                 final JsonObject indexExpected = new JsonObject(
                     "{\"status\":\"SUCCESS\",\"action\":\"GET_LIST\",\"data\":{\"apis\":[{\"name\":\"http.test\"," +
                     "\"status\":\"UP\",\"type\":\"http-endpoint\",\"location\":\"http://123.456.0.1:1234/api\"}]}}");
                 client.request(config.getGatewayConfig().getIndexAddress(), EventMessage.initial(EventAction.GET_LIST))
                       .onSuccess(msg -> JsonHelper.assertJson(context, async, indexExpected, msg.toJson(),
                                                               JSONCompareMode.LENIENT));
             });
    }

    @Test
    public void test_serviceDiscovery_local_register_eventMessage(TestContext context) {
        final Async async = context.async(2);
        final MicroConfig config = IConfig.fromClasspath("local.json", MicroConfig.class);
        micro = new MicroContext().setup(vertx, config);
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
        EventBusHelper.assertReceivedData(vertx, async, micro.getLocalInvoker().getConfig().getAnnounceAddress(),
                                          JsonHelper.asserter(context, async, expected, JSONCompareMode.LENIENT));
        EventBusClient client = EventBusClient.create(SharedDataLocalProxy.create(vertx, MicroContext.class.getName()));
        micro.getLocalInvoker()
             .addRecord(EventMessageService.createRecord("event-message", "address.1",
                                                         EventMethodDefinition.createDefault("/path", "/:param")))
             .onSuccess(record -> {
                 final JsonObject indexExpected = new JsonObject("{\"status\":\"SUCCESS\",\"action\":\"GET_LIST\"," +
                                                                 "\"data\":{\"apis\":[{\"name\":\"event-message\"," +
                                                                 "\"status\":\"UP\",\"location\":\"address.1\"," +
                                                                 "\"endpoints\":[{\"method\":\"GET\"," +
                                                                 "\"path\":\"/path\"},{\"method\":\"PATCH\"," +
                                                                 "\"path\":\"/path/:param\"},{\"method\":\"PUT\"," +
                                                                 "\"path\":\"/path/:param\"},{\"method\":\"POST\"," +
                                                                 "\"path\":\"/path\"},{\"method\":\"DELETE\"," +
                                                                 "\"path\":\"/path/:param\"},{\"method\":\"GET\"," +
                                                                 "\"path\":\"/path/:param\"}]}]}}");
                 client.request(config.getGatewayConfig().getIndexAddress(), EventMessage.initial(EventAction.GET_LIST))
                       .onSuccess(msg -> JsonHelper.assertJson(context, async, indexExpected, msg.toJson(),
                                                               JSONCompareMode.LENIENT));
             });
    }

}

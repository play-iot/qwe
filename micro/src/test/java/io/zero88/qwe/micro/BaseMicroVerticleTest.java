package io.zero88.qwe.micro;

import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.http.EventMethodDefinition;

@RunWith(VertxUnitRunner.class)
public abstract class BaseMicroVerticleTest {

    public static final String EVENT_RECORD_1 = "event.record.1";
    public static final String EVENT_ADDRESS_1 = "event.address.1";
    public static final String EVENT_RECORD_2 = "event.record.2";
    public static final String EVENT_ADDRESS_2 = "event.address.2";
    public static final String HTTP_RECORD = "http.test";
    protected MicroConfig config;
    protected EventBusClient eventbus;
    private Vertx vertx;
    private MicroContext micro;

    @BeforeClass
    public static void init() {
        TestHelper.setup();
    }

    @Before
    public void setup(TestContext context) {
        Async async = context.async(3);
        config = IConfig.fromClasspath("local.json", MicroConfig.class);
        vertx = Vertx.vertx();
        micro = new MicroContext().setup(vertx, config);
        eventbus = EventBusClient.create(SharedDataLocalProxy.create(vertx, micro.sharedKey()));
        final ServiceDiscoveryInvoker discovery = micro.getLocalInvoker();
        final Future<Record> record1 = discovery.addHttpRecord(HTTP_RECORD, new HttpLocation().setHost("123.456.0.1")
                                                                                              .setPort(1234)
                                                                                              .setRoot("/api"),
                                                               new JsonObject().put("meta", "test"));
        final Future<Record> record2 = discovery.addEventMessageRecord(EVENT_RECORD_1, EVENT_ADDRESS_1,
                                                                       EventMethodDefinition.createDefault("/path",
                                                                                                           "/:param"));
        final Future<Record> record3 = discovery.addEventMessageRecord(EVENT_RECORD_2, EVENT_ADDRESS_2,
                                                                       EventMethodDefinition.createDefault("/xy",
                                                                                                           "/:z"));
        CompositeFuture.all(record1, record2, record3)
                       .onSuccess(record -> TestHelper.testComplete(async))
                       .onFailure(context::fail);
    }

    @After
    public void tearDown(TestContext context) {
        if (Objects.nonNull(micro)) {
            micro.unregister(Promise.promise());
        }
        vertx.close(context.asyncAssertSuccess());
    }

}

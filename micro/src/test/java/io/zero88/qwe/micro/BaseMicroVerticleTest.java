package io.zero88.qwe.micro;

import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.ComponentTestHelper;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.http.EventMethodDefinition;

@RunWith(VertxUnitRunner.class)
public abstract class BaseMicroVerticleTest implements ComponentTestHelper {

    public static final String EVENT_RECORD_1 = "event.record.1";
    public static final String EVENT_ADDRESS_1 = "event.address.1";
    public static final String EVENT_RECORD_2 = "event.record.2";
    public static final String EVENT_ADDRESS_2 = "event.address.2";
    public static final String HTTP_RECORD = "http.test";
    protected MicroConfig config;
    protected EventBusClient ebClient;
    protected MicroContext micro;
    private Vertx vertx;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void init() {
        TestHelper.setup();
    }

    @Override
    public Path testDir() {
        return folder.getRoot().toPath();
    }

    @Before
    public void setup(TestContext context) {
        config = IConfig.fromClasspath("local.json", MicroConfig.class);
        vertx = Vertx.vertx();
        micro = deploy(vertx, context, config.toJson(), new MicroVerticleProvider()).componentContext();
        ebClient = EventBusClient.create(createSharedData(vertx));
        ServiceDiscoveryWrapper discovery = micro.getDiscovery();
        Future<Record> record1 = discovery.addRecord(HTTP_RECORD, new HttpLocation().setHost("123.456.0.1")
                                                                                    .setPort(1234)
                                                                                    .setRoot("/api"),
                                                     new JsonObject().put("meta", "test"));
        Future<Record> record2 = discovery.addRecord(EVENT_RECORD_1, EVENT_ADDRESS_1,
                                                     EventMethodDefinition.createDefault("/path", "/:param"));
        Future<Record> record3 = discovery.addRecord(EVENT_RECORD_2, EVENT_ADDRESS_2,
                                                     EventMethodDefinition.createDefault("/xy", "/:z"));
        Async async = context.async();
        CompositeFuture.all(record1, record2, record3)
                       .onSuccess(record -> TestHelper.testComplete(async))
                       .onFailure(context::fail);
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

}

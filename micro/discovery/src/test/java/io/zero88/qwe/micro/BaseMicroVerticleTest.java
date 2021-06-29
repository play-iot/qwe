package io.zero88.qwe.micro;

import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.ComponentTestHelper;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.http.EventMethodDefinition;

@ExtendWith(VertxExtension.class)
public abstract class BaseMicroVerticleTest implements ComponentTestHelper {

    public static final String EVENT_RECORD_1 = "event.record.1";
    public static final String EVENT_ADDRESS_1 = "event.address.1";
    public static final String EVENT_RECORD_2 = "event.record.2";
    public static final String EVENT_ADDRESS_2 = "event.address.2";
    public static final String HTTP_RECORD = "http.test";
    protected MicroConfig config;
    protected EventBusClient ebClient;
    protected MicroContext microContext;
    @TempDir
    public Path folder;

    @BeforeAll
    public static void init() {
        TestHelper.setup();
    }

    @Override
    public Path testDir() {
        return folder.getRoot();
    }

    @BeforeEach
    public void setup(Vertx vertx, VertxTestContext context) {
        config = initMicroConfig();
        microContext = deploy(vertx, context, config.toJson(), new MicroVerticleProvider()).componentContext();
        ebClient = EventBusClient.create(createSharedData(vertx));
    }

    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext context) {
        vertx.close(context.succeedingThenComplete());
    }

    protected MicroConfig initMicroConfig() {
        return IConfig.fromClasspath("local.json", MicroConfig.class);
    }

    protected void registerService(VertxTestContext context) {
        ServiceDiscoveryApi discovery = microContext.getDiscovery();
        Checkpoint cp = context.checkpoint();
        CompositeFuture.all(Stream.of(
            RecordHelper.create(HTTP_RECORD, new HttpLocation().setHost("123.456.0.1").setPort(1234).setRoot("/api"),
                                new JsonObject().put("meta", "test")),
            RecordHelper.create(EVENT_RECORD_1, EVENT_ADDRESS_1,
                                EventMethodDefinition.createDefault("/path", "/:param")),
            RecordHelper.create(EVENT_RECORD_2, EVENT_ADDRESS_2, EventMethodDefinition.createDefault("/xy", "/:z")))
                                  .map(discovery::register)
                                  .collect(Collectors.toList()))
                       .onSuccess(record -> cp.flag())
                       .onFailure(context::failNow);
    }

    protected ServiceGatewayConfig getGatewayConfig() {
        return Objects.requireNonNull(config.lookup(ServiceGatewayConfig.NAME, ServiceGatewayConfig.class));
    }

}

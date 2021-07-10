package io.zero88.qwe.http.server.dynamic;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.Before;
import org.skyscreamer.jsonassert.Customization;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.zero88.qwe.ApplicationVerticle;
import io.zero88.qwe.DeployContext;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.VertxHelper;
import io.zero88.qwe.http.server.HttpServerPluginTestBase;
import io.zero88.qwe.http.server.dynamic.mock.MockGatewayServer;

public abstract class DynamicServiceTestBase extends HttpServerPluginTestBase {

    @Before
    public void before(TestContext context) throws IOException {
        super.before(context);
        startGatewayAndService(context, service(), getServiceOptions());
    }

    protected void startGatewayAndService(TestContext context, ApplicationVerticle service,
                                          DeploymentOptions serviceOptions) {
        CountDownLatch latch = new CountDownLatch(2);
        DeploymentOptions options = new DeploymentOptions().setConfig(deployConfig(httpConfig.getPort()));
        VertxHelper.deploy(vertx, context,
                           DeployContext.builder().options(options).verticle(gateway().get()).successAsserter(id -> {
                               System.out.println("Gateway Deploy Id: " + id);
                               latch.countDown();
                               VertxHelper.deploy(vertx, context, DeployContext.builder()
                                                                               .options(serviceOptions)
                                                                               .verticle(service)
                                                                               .successAsserter(d -> latch.countDown())
                                                                               .build());
                           }).build());
        long start = System.nanoTime();
        try {
            context.assertTrue(latch.await(timeoutInSecond(), TimeUnit.SECONDS),
                               "Timeout when deploying gateway and service verticle");
        } catch (InterruptedException e) {
            context.fail("Failed to start Gateway and HTTP Service");
        }
        //small delay for enable dynamic api
        TestHelper.sleep(500);
        System.out.println("FINISHED AFTER: " + (System.nanoTime() - start) / 1e9);
    }

    protected JsonObject deployConfig(int port) {
        return new JsonObject().put("__app__", new JsonObject().put("__http__", new JsonObject().put("port", port)));
    }

    @SuppressWarnings("unchecked")
    protected <T extends ApplicationVerticle> Supplier<T> gateway() {
        return () -> (T) new MockGatewayServer();
    }

    protected DeploymentOptions getServiceOptions() throws IOException {
        return new DeploymentOptions();
    }

    protected abstract <T extends ApplicationVerticle> T service();

    protected int timeoutInSecond() {
        return TestHelper.TEST_TIMEOUT_SEC;
    }

}

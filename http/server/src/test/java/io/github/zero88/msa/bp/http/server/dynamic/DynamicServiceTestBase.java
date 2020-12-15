package io.github.zero88.msa.bp.http.server.dynamic;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.Before;
import org.skyscreamer.jsonassert.Customization;

import io.github.zero88.msa.bp.http.server.HttpServerTestBase;
import io.github.zero88.msa.bp.http.server.dynamic.mock.MockGatewayServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

import io.github.zero88.msa.bp.TestHelper;
import io.github.zero88.msa.bp.TestHelper.VertxHelper;
import io.github.zero88.msa.bp.component.ContainerVerticle;

public abstract class DynamicServiceTestBase extends HttpServerTestBase {

    static final Customization IGNORE_URI = new Customization("message.uri", (o1, o2) -> true);

    @Before
    public void before(TestContext context) throws IOException {
        super.before(context);
        startGatewayAndService(context, service(), getServiceOptions());
    }

    protected void startGatewayAndService(TestContext context, ContainerVerticle service,
                                          DeploymentOptions serviceOptions) {
        CountDownLatch latch = new CountDownLatch(2);
        DeploymentOptions config = new DeploymentOptions().setConfig(deployConfig(httpConfig.getPort()));
        VertxHelper.deploy(vertx.getDelegate(), context, config, gateway().get(), id -> {
            System.out.println("Gateway Deploy Id: " + id);
            latch.countDown();
            VertxHelper.deploy(vertx.getDelegate(), context, serviceOptions, service, d -> {
                System.out.println("Service Deploy Id: " + d);
                latch.countDown();
            });
        });
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
    protected <T extends ContainerVerticle> Supplier<T> gateway() {
        return () -> (T) new MockGatewayServer();
    }

    protected DeploymentOptions getServiceOptions() throws IOException {
        return new DeploymentOptions();
    }

    protected abstract <T extends ContainerVerticle> T service();

    protected int timeoutInSecond() {
        return TestHelper.TEST_TIMEOUT_SEC;
    }

}

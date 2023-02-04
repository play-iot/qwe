package cloud.playio.qwe.http.server.proxy;

import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

import org.junit.Before;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import cloud.playio.qwe.ApplicationVerticle;
import cloud.playio.qwe.DeployContext;
import cloud.playio.qwe.TestHelper;
import cloud.playio.qwe.VertxHelper;
import cloud.playio.qwe.http.server.HttpServerPluginTestBase;
import cloud.playio.qwe.http.server.RestApiTestHelper;
import cloud.playio.qwe.http.server.proxy.mock.MockGatewayServer;

public abstract class ProxyServiceTestBase extends HttpServerPluginTestBase implements RestApiTestHelper {

    @Before
    public void before(TestContext context) {
        super.before(context);
        startGatewayAndService(context, service(), getServiceOptions());
    }

    protected void startGatewayAndService(TestContext context, ApplicationVerticle app,
                                          DeploymentOptions serviceOptions) {
        CountDownLatch c1 = new CountDownLatch(1);
        DeploymentOptions options = new DeploymentOptions().setConfig(deployConfig(httpConfig.getPort()));
        ApplicationVerticle gateway = gateway().get();
        VertxHelper.deploy(vertx, context, DeployContext.builder()
                                                        .options(options)
                                                        .verticle(gateway)
                                                        .successAsserter(id -> c1.countDown())
                                                        .build());
        TestHelper.waitTimeout(timeoutInSecond(), c1);
        CountDownLatch c2 = new CountDownLatch(1);
        VertxHelper.deploy(vertx, context, DeployContext.builder()
                                                        .options(serviceOptions)
                                                        .verticle(app)
                                                        .successAsserter(d -> c2.countDown())
                                                        .build());
        //small delay for enable dynamic api
        TestHelper.sleep(500);
        TestHelper.waitTimeout(timeoutInSecond(), c2);
    }

    protected JsonObject deployConfig(int port) {
        return new JsonObject().put("__app__", new JsonObject().put("__http__", new JsonObject().put("port", port)));
    }

    @SuppressWarnings("unchecked")
    protected <T extends ApplicationVerticle> Supplier<T> gateway() {
        return () -> (T) new MockGatewayServer();
    }

    protected DeploymentOptions getServiceOptions() {
        return new DeploymentOptions();
    }

    protected abstract <T extends ApplicationVerticle> T service();

    protected int timeoutInSecond() {
        return TestHelper.TEST_TIMEOUT_SEC;
    }

}

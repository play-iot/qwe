package cloud.playio.qwe.http.server;

import java.nio.file.Path;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import cloud.playio.qwe.BasePluginTest.PluginDeployTest;
import cloud.playio.qwe.IConfig;
import cloud.playio.qwe.PluginDeploymentHelper;
import cloud.playio.qwe.PluginProvider;
import cloud.playio.qwe.TestHelper;
import cloud.playio.qwe.http.client.HttpClientConfig;
import cloud.playio.qwe.http.client.HttpClientExtension;
import cloud.playio.qwe.http.client.HttpClientWrapper;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RunWith(VertxUnitRunner.class)
public abstract class HttpServerPluginTestBase implements PluginDeployTest<HttpServerPlugin>, HttpServerTestHelper {

    protected static final String DEFAULT_HOST = "127.0.0.1";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Getter
    protected Vertx vertx;
    protected HttpServerConfig httpConfig;
    @Getter
    protected HttpClientWrapper client;
    protected RequestOptions requestOptions;

    @BeforeClass
    public static void beforeSuite() {TestHelper.setup();}

    @Override
    public Path testDir() {
        return folder.getRoot().toPath();
    }

    @Override
    public String appName() {
        return PluginDeployTest.super.appName();
    }

    @Before
    public void before(TestContext context) {
        vertx = Vertx.vertx();
        client = createHttpClient();
        httpConfig = initConfig();
        requestOptions = new RequestOptions().setHost(DEFAULT_HOST).setPort(httpConfig.getPort());
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    protected String httpConfigFile() {
        return "httpServer.json";
    }

    protected HttpClientWrapper createHttpClient() {
        final HttpClientConfig extConfig = new HttpClientConfig().setOptions(
            new HttpClientOptions().setConnectTimeout(TestHelper.TEST_TIMEOUT_SEC * 1000));
        return initExtension(vertx, HttpClientExtension.class, extConfig).entrypoint();
    }

    protected HttpServerPlugin startServer(TestContext context, HttpServerRouter httpRouter) {
        return PluginDeploymentHelper.Junit4.create(this)
                                            .deploy(vertx, context, httpConfig,
                                                    new HttpServerPluginProvider(httpRouter));
    }

    protected void startFailed(TestContext context, HttpServerRouter httpRouter, Consumer<Throwable> consumer) {
        PluginDeploymentHelper.Junit4.create(this)
                                     .deployFailed(vertx, context, httpConfig, new HttpServerPluginProvider(httpRouter),
                                                   consumer);
    }

    @Override
    public HttpServerConfig initConfig() {
        return IConfig.fromClasspath(httpConfigFile(), HttpServerConfig.class)
                      .setHost(DEFAULT_HOST)
                      .setPort(TestHelper.getRandomPort());
    }

    @Override
    public PluginProvider<HttpServerPlugin> initProvider() {
        throw new UnsupportedOperationException("Init plugin per test");
    }

    public RequestOptions requestOptions() {
        return new RequestOptions(requestOptions);
    }

}

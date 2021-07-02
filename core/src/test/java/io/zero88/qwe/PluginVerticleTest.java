package io.zero88.qwe;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.zero88.qwe.MockProvider.MockPlugin;
import io.zero88.qwe.exceptions.ConfigException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(VertxUnitRunner.class)
public class PluginVerticleTest implements PluginTestHelper {

    private Vertx vertx;

    @BeforeClass
    public static void beforeSuite() {
        TestHelper.setup();
        ((Logger) LoggerFactory.getLogger("io.github.zero88")).setLevel(Level.TRACE);
    }

    @Before
    public void before() {
        vertx = Vertx.vertx();
    }

    @Test
    public void not_have_config_file_should_deploy_success(TestContext context) {
        MockPlugin plugin = new MockPlugin(createSharedData(vertx));
        Async async = context.async();
        VertxHelper.deploy(vertx, context, DeployContext.builder()
                                                        .verticle(plugin)
                                                        .successAsserter(id -> TestHelper.testComplete(async))
                                                        .build());
    }

    @Test
    public void invalid_config_should_deploy_failed(TestContext context) {
        MockPlugin plugin = new MockPlugin(createSharedData(vertx));
        Async async = context.async();
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("xx", "yyy"));
        VertxHelper.deployFailed(vertx, context, options, plugin, t -> {
            TestHelper.testComplete(async);
            Assert.assertEquals("Invalid configuration format", t.getMessage());
            TestHelper.assertCause(() -> { throw t; }, ConfigException.class, IllegalArgumentException.class);
        });
    }

    @Test
    @Ignore("Need the information from Zero")
    public void test_register_shared_data(TestContext context) {
        MockPlugin plugin = new MockPlugin(createSharedData(vertx));
        final String key = MockPlugin.class.getName();
        //        plugin.setup(key);

        Async async = context.async();
        VertxHelper.deploy(vertx, context, DeployContext.builder()
                                                        .verticle(plugin)
                                                        .successAsserter(id -> TestHelper.testComplete(async))
                                                        .build());
    }

    @Test
    public void throw_unexpected_error_cannot_start(TestContext context) {
        MockPlugin plugin = new MockPlugin(createSharedData(vertx), true);
        Async async = context.async();
        DeploymentOptions options = new DeploymentOptions();
        VertxHelper.deployFailed(vertx, context, options, plugin, t -> {
            TestHelper.testComplete(async);
            Assert.assertTrue(t instanceof RuntimeException);
            Assert.assertEquals(0, vertx.deploymentIDs().size());
        });
    }

    @After
    public void after() { vertx.close(); }

    @Override
    public Path testDir() {
        return Paths.get("/tmp");
    }

}


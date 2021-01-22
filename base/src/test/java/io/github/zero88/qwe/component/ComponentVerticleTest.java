package io.github.zero88.qwe.component;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.TestHelper;
import io.github.zero88.qwe.TestHelper.VertxHelper;
import io.github.zero88.qwe.exceptions.CarlException;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(VertxUnitRunner.class)
public class ComponentVerticleTest {

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
        MockComponent unitVerticle = new MockComponent();
        Async async = context.async();
        VertxHelper.deploy(vertx, context, new DeploymentOptions(), unitVerticle, deployId -> {
            context.assertNotNull(deployId);
            TestHelper.testComplete(async);
        });
    }

    @Test
    public void invalid_config_should_deploy_failed(TestContext context) {
        MockComponent unitVerticle = new MockComponent();
        Async async = context.async();
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("xx", "yyy"));
        VertxHelper.deployFailed(vertx, context, options, unitVerticle, t -> {
            TestHelper.testComplete(async);
            Assert.assertTrue(t instanceof CarlException);
            Assert.assertEquals("Invalid config format", t.getMessage());
        });
    }

    @Test
    @Ignore("Need the information from Zero")
    public void test_register_shared_data(TestContext context) {
        MockComponent unitVerticle = new MockComponent();
        final String key = MockComponent.class.getName();
        unitVerticle.registerSharedKey(key);

        Async async = context.async();
        DeploymentOptions options = new DeploymentOptions();
        VertxHelper.deploy(vertx, context, options, unitVerticle, t -> {
            unitVerticle.getSharedData(key);
            TestHelper.testComplete(async);
        });
    }

    @Test
    public void throw_unexpected_error_cannot_start(TestContext context) {
        MockComponent unitVerticle = new MockComponent(true);
        Async async = context.async();
        DeploymentOptions options = new DeploymentOptions();
        VertxHelper.deployFailed(vertx, context, options, unitVerticle, t -> {
            TestHelper.testComplete(async);
            Assert.assertTrue(t instanceof RuntimeException);
            Assert.assertEquals(0, vertx.deploymentIDs().size());
        });
    }

    @After
    public void after() { vertx.close(); }

}

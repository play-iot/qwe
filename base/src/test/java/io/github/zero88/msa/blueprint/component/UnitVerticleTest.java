package io.github.zero88.msa.blueprint.component;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import io.github.zero88.msa.blueprint.TestHelper;
import io.github.zero88.msa.blueprint.TestHelper.VertxHelper;
import io.github.zero88.msa.blueprint.exceptions.BlueprintException;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(VertxUnitRunner.class)
public class UnitVerticleTest {

    private Vertx vertx;

    @BeforeClass
    public static void beforeSuite() {
        TestHelper.setup();
        ((Logger) LoggerFactory.getLogger("io.zero88")).setLevel(Level.TRACE);
    }

    @Before
    public void before() {
        vertx = Vertx.vertx();
    }

    @Test
    public void not_have_config_file_should_deploy_success(TestContext context) {
        MockUnitVerticle unitVerticle = new MockUnitVerticle();
        Async async = context.async();
        VertxHelper.deploy(vertx, context, new DeploymentOptions(), unitVerticle, deployId -> {
            context.assertNotNull(deployId);
            TestHelper.testComplete(async);
        });
    }

    @Test
    public void invalid_config_should_deploy_failed(TestContext context) {
        MockUnitVerticle unitVerticle = new MockUnitVerticle();
        Async async = context.async();
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("xx", "yyy"));
        VertxHelper.deployFailed(vertx, context, options, unitVerticle, t -> {
            TestHelper.testComplete(async);
            Assert.assertTrue(t instanceof BlueprintException);
            Assert.assertEquals("Invalid config format", t.getMessage());
        });
    }

    @Test
    @Ignore("Need the information from Zero")
    public void test_register_shared_data(TestContext context) {
        MockUnitVerticle unitVerticle = new MockUnitVerticle();
        final String key = MockUnitVerticle.class.getName();
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
        MockUnitVerticle unitVerticle = new MockUnitVerticle(true);
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


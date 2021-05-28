package io.zero88.qwe;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.impl.Deployment;
import io.vertx.core.impl.VertxImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.zero88.qwe.exceptions.QWEException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(VertxUnitRunner.class)
public class ApplicationVerticleTest {

    private Vertx vertx;
    private MockApplication application;

    @BeforeClass
    public static void beforeSuite() {
        TestHelper.setup();
        ((Logger) LoggerFactory.getLogger("io.zero88")).setLevel(Level.TRACE);
    }

    @Before
    public void before() {
        vertx = Vertx.vertx();
        application = new MockApplication();
    }

    @Test
    public void test_compute_deployment_option_per_component(TestContext context) {
        Async async = context.async(2);
        addDummyUnit();
        addMockUnit();
        application.setOnCompletedHandler(lookup -> TestHelper.testComplete(async));
        VertxHelper.deploy(vertx, context, new DeploymentOptions().setWorkerPoolSize(15), application, deployId -> {
            context.assertNotNull(deployId);
            TestHelper.testComplete(async);
            Assert.assertEquals(3, vertx.deploymentIDs().size());
            vertx.deploymentIDs()
                 .stream()
                 .map(id -> ((VertxImpl) vertx).getDeployment(id))
                 .filter(Deployment::isChild)
                 .map(Deployment::deploymentOptions)
                 .forEach(opt -> {
                     Assert.assertEquals(7, opt.getWorkerPoolSize());
                     Assert.assertTrue(opt.getWorkerPoolName().startsWith(Application.DEFAULT_COMPONENT_THREAD_PREFIX));
                 });
        });
    }

    @Test
    public void test_use_deployment_option_from_component(TestContext context) {
        Async async = context.async(2);
        addDummyUnit();
        application.setOnCompletedHandler(lookup -> TestHelper.testComplete(async));
        JsonObject config = new QWEAppConfig().put(new MockConfig().deploymentKey(),
                                                   new DeploymentOptions().setHa(true)
                                                                          .setWorkerPoolSize(3)
                                                                          .setWorkerPoolName("test")
                                                                          .toJson()).toJson();
        VertxHelper.deploy(vertx, context, new DeploymentOptions().setConfig(config), application,
                           deployId -> {
                               context.assertNotNull(deployId);
                               TestHelper.testComplete(async);
                               Assert.assertEquals(2, vertx.deploymentIDs().size());
                               vertx.deploymentIDs()
                                    .stream()
                                    .map(id -> ((VertxImpl) vertx).getDeployment(id))
                                    .filter(Deployment::isChild)
                                    .map(Deployment::deploymentOptions)
                                    .forEach(opt -> {
                                        Assert.assertTrue(opt.isHa());
                                        Assert.assertEquals(3, opt.getWorkerPoolSize());
                                        Assert.assertEquals("test", opt.getWorkerPoolName());
                                    });
                           });
    }

    @Test
    public void test_contain_two_component_vertical_having_same_type_should_deploy_only_one(TestContext context) {
        Async async = context.async(2);
        addDummyUnit();
        addDummyUnit();
        application.setOnCompletedHandler(lookup -> TestHelper.testComplete(async));
        VertxHelper.deploy(vertx, context, new DeploymentOptions(), application, deployId -> {
            context.assertNotNull(deployId);
            TestHelper.testComplete(async);
            Assert.assertEquals(2, vertx.deploymentIDs().size());
        });
    }

    @Test
    public void test_contain_two_component_vertical_having_different_type_should_deploy_both(TestContext context) {
        Async async = context.async(2);
        addDummyUnit();
        addMockUnit();
        application.setOnCompletedHandler(lookup -> TestHelper.testComplete(async));
        VertxHelper.deploy(vertx, context, new DeploymentOptions(), application, deployId -> {
            context.assertNotNull(deployId);
            TestHelper.testComplete(async);
            Assert.assertEquals(3, vertx.deploymentIDs().size());
        });
    }

    @Test
    public void test_cannot_start_coz_throw_exception_onStart(TestContext context) {
        application.setErrorOnStart(true);
        addDummyUnit();
        assertDeployError(context, new QWEException("UNKNOWN_ERROR | Cause: Error when starting"));
    }

    @Test
    public void test_cannot_start_coz_install_comp_that_throw_exception(TestContext context) {
        addDummyUnit();
        addMockUnitHavingException();
        assertDeployError(context, new QWEException("UNKNOWN_ERROR | Cause: Error when starting Component Verticle"));
    }

    @Test
    public void test_start_but_throw_exception_onInstallCompleted(TestContext context) {
        Async async = context.async();
        application.setErrorOnCompleted(true);
        addDummyUnit();
        VertxHelper.deploy(vertx, context, new DeploymentOptions(), application, deployId -> {
            context.assertNotNull(deployId);
            TestHelper.testComplete(async);
            Assert.assertEquals(2, vertx.deploymentIDs().size());
        });
    }

    private void assertDeployError(TestContext context, Throwable error) {
        Async async = context.async();
        VertxHelper.deployFailed(vertx, context, new DeploymentOptions(), application, t -> {
            try {
                Assert.assertTrue(error.getClass().isInstance(t));
                Assert.assertEquals(error.getMessage(), t.getMessage());
                Assert.assertEquals(0, vertx.deploymentIDs().size());
            } finally {
                TestHelper.testComplete(async);
            }
        });
    }

    private void addMockUnit() {
        addMockUnit(false);
    }

    private void addMockUnitHavingException() {
        addMockUnit(true);
    }

    private void addMockUnit(boolean error) {
        application.addProvider(new MockProvider(error));
    }

    private void addDummyUnit() {
        application.addProvider(new DummyProvider());
    }

    @After
    public void after() {
        vertx.close();
    }

}

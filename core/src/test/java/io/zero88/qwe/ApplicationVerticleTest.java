package io.zero88.qwe;

import java.util.function.Consumer;

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
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.zero88.qwe.exceptions.QWEException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(VertxUnitRunner.class)
public class ApplicationVerticleTest {

    private Vertx vertx;
    private MockApplication app;

    @BeforeClass
    public static void beforeSuite() {
        TestHelper.setup();
        ((Logger) LoggerFactory.getLogger("io.zero88")).setLevel(Level.TRACE);
    }

    @Before
    public void before() {
        vertx = Vertx.vertx();
        app = new MockApplication();
    }

    @Test
    public void test_compute_deployment_option_per_plugin(TestContext context) {
        Async async = context.async(2);
        addDummyUnit();
        addMockUnit();
        Consumer<String> asserter = deployId -> {
            TestHelper.testComplete(async);
            Assert.assertEquals(3, vertx.deploymentIDs().size());
            vertx.deploymentIDs()
                 .stream()
                 .map(id -> ((VertxImpl) vertx).getDeployment(id))
                 .filter(Deployment::isChild)
                 .map(Deployment::deploymentOptions)
                 .forEach(opt -> {
                     Assert.assertEquals(7, opt.getWorkerPoolSize());
                     Assert.assertTrue(opt.getWorkerPoolName().startsWith(Application.DEFAULT_PLUGIN_THREAD_PREFIX));
                 });
        };
        VertxHelper.deploy(vertx, context, DeployContext.builder()
                                                        .verticle(app.onCompleted(i -> TestHelper.testComplete(async)))
                                                        .options(new DeploymentOptions().setWorkerPoolSize(15))
                                                        .successAsserter(asserter)
                                                        .build());
    }

    @Test
    public void test_use_deployment_option_from_plugin(TestContext context) {
        Async async = context.async(2);
        addDummyUnit();
        QWEAppConfig config = new QWEAppConfig().put(new MockConfig().deploymentKey(),
                                                     new DeploymentOptions().setHa(true)
                                                                            .setWorkerPoolSize(3)
                                                                            .setWorkerPoolName("test")
                                                                            .toJson());
        Consumer<String> asserter = deployId -> {
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
        };
        VertxHelper.deploy(vertx, context, DeployContext.builder()
                                                        .verticle(app.onCompleted(i -> TestHelper.testComplete(async)))
                                                        .options(new DeploymentOptions().setConfig(config.toJson()))
                                                        .successAsserter(asserter)
                                                        .build());
    }

    @Test
    public void test_contain_two_plugin_vertical_having_same_type_should_deploy_only_one(TestContext context) {
        Async async = context.async(2);
        addDummyUnit();
        addDummyUnit();
        VertxHelper.deploy(vertx, context, DeployContext.builder()
                                                        .verticle(app.onCompleted(i -> TestHelper.testComplete(async)))
                                                        .successAsserter(deployId -> {
                                                            TestHelper.testComplete(async);
                                                            Assert.assertEquals(2, vertx.deploymentIDs().size());
                                                        })
                                                        .build());
    }

    @Test
    public void test_contain_two_plugin_vertical_having_different_type_should_deploy_both(TestContext context) {
        Async async = context.async(2);
        addDummyUnit();
        addMockUnit();
        VertxHelper.deploy(vertx, context, DeployContext.builder()
                                                        .verticle(app.onCompleted(i -> TestHelper.testComplete(async)))
                                                        .successAsserter(i -> {
                                                            TestHelper.testComplete(async);
                                                            Assert.assertEquals(3, vertx.deploymentIDs().size());
                                                        })
                                                        .build());
    }

    @Test
    public void test_cannot_start_coz_throw_exception_onStart(TestContext context) {
        app.errorOnStart(true);
        addDummyUnit();
        assertDeployError(context, new QWEException("UNKNOWN_ERROR | Cause: Error when starting"));
    }

    @Test
    public void test_cannot_start_coz_install_comp_that_throw_exception(TestContext context) {
        addDummyUnit();
        addMockUnitHavingException();
        assertDeployError(context, new QWEException("UNKNOWN_ERROR | Cause: Error when starting plugin Verticle"));
    }

    @Test
    public void test_start_but_throw_exception_onInstallCompleted(TestContext context) {
        Async async = context.async();
        addDummyUnit();
        VertxHelper.deploy(vertx, context,
                           DeployContext.builder().verticle(app.errorOnCompleted(true)).successAsserter(i -> {
                               TestHelper.testComplete(async);
                               Assert.assertEquals(2, vertx.deploymentIDs().size());
                           }).build());
    }

    private void assertDeployError(TestContext context, Throwable error) {
        Async async = context.async();
        VertxHelper.deploy(vertx, context, DeployContext.builder().verticle(app).failedAsserter(t -> {
            try {
                Assert.assertTrue(error.getClass().isInstance(t));
                Assert.assertEquals(error.getMessage(), t.getMessage());
                Assert.assertEquals(0, vertx.deploymentIDs().size());
            } finally {
                TestHelper.testComplete(async);
            }
        }).build());
    }

    private void addMockUnit() {
        addMockUnit(false);
    }

    private void addMockUnitHavingException() {
        addMockUnit(true);
    }

    private void addMockUnit(boolean error) {
        app.addProvider(new MockProvider(error));
    }

    private void addDummyUnit() {
        app.addProvider(new DummyProvider());
    }

    @After
    public void after() {
        vertx.close();
    }

}

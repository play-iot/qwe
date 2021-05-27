package io.zero88.qwe;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.junit5.VertxTestContext;

@SuppressWarnings( {"unchecked", "rawtypes"})
public interface ComponentTestHelper {

    static <T extends Component> SharedDataLocalProxy createSharedData(Vertx vertx, Class<T> aClass) {
        return SharedDataLocalProxy.create(vertx, aClass.getName());
    }

    static <T extends Component> T deploy(Vertx vertx, VertxTestContext context, JsonObject config,
                                          ComponentProvider<T> provider, Path testDir) throws InterruptedException {
        final T component = initComponent(vertx, provider, testDir);
        CountDownLatch latch = new CountDownLatch(1);
        vertx.deployVerticle(component, new DeploymentOptions().setConfig(config), s -> {
            latch.countDown();
            if (s.failed()) {
                context.failNow(s.cause());
                return;
            }
            setup(component, testDir, provider.componentClass().getName(), s.result());
            context.completeNow();
        });
        latch.await();
        return component;
    }

    static <T extends Component> T deploy(Vertx vertx, TestContext context, JsonObject config,
                                          ComponentProvider<T> provider, Path testDir) {
        final T component = initComponent(vertx, provider, testDir);
        return VertxHelper.deploy(vertx, context, new DeploymentOptions().setConfig(config), component,
                                  TestHelper.TEST_TIMEOUT_SEC,
                                  s -> setup(component, testDir, provider.componentClass().getName(), s));
    }

    static <T extends Component> T initComponent(Vertx vertx, ComponentProvider<T> provider, Path testDir) {
        final SharedDataLocalProxy proxy = createSharedData(vertx, provider.componentClass());
        proxy.addData(SharedDataLocalProxy.APP_DATADIR, testDir.toString());
        return provider.provide(proxy);
    }

    static <T extends Component> void deployFailed(Vertx vertx, TestContext context, JsonObject config,
                                                   ComponentProvider<T> provider, Handler<Throwable> handler) {
        VertxHelper.deployFailed(vertx, context, new DeploymentOptions().setConfig(config),
                                 provider.provide(createSharedData(vertx, provider.componentClass())), handler);
    }

    static <T extends Component> void setup(T comp, Path testDir, String sharedKey, String result) {
        comp.setup(comp.hook().onSuccess(ComponentContext.create(comp.appName(), testDir, sharedKey, result)));
    }

}

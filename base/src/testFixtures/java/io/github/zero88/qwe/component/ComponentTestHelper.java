package io.github.zero88.qwe.component;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import io.github.zero88.qwe.TestHelper;
import io.github.zero88.qwe.VertxHelper;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.junit5.VertxTestContext;

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
            final ComponentContext ctx = ComponentContext.create(component.getClass(), testDir,
                                                                 provider.componentClass().getName(), s.result());
            component.setup(component.hook().onSuccess(ctx));
            context.completeNow();
        });
        latch.await();
        return component;
    }

    @SuppressWarnings("unchecked")
    static <T extends Component> T deploy(Vertx vertx, TestContext context, JsonObject config,
                                          ComponentProvider<T> provider, Path testDir) {
        final T component = initComponent(vertx, provider, testDir);
        return VertxHelper.deploy(vertx, context, new DeploymentOptions().setConfig(config), component,
                                  TestHelper.TEST_TIMEOUT_SEC, s -> {
                final ComponentContext ctx = ComponentContext.create(component.getClass(), testDir,
                                                                     provider.componentClass().getName(), s);
                component.setup(component.hook().onSuccess(ctx));
            });
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

}

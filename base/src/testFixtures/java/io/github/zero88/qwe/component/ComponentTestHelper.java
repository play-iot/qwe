package io.github.zero88.qwe.component;

import java.nio.file.Path;

import io.github.zero88.qwe.TestHelper;
import io.github.zero88.qwe.VertxHelper;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

public interface ComponentTestHelper {

    static <T extends Component> SharedDataLocalProxy createSharedData(Vertx vertx, Class<T> aClass) {
        return SharedDataLocalProxy.create(vertx, aClass.getName());
    }

    @SuppressWarnings("unchecked")
    static <T extends Component> T deploy(Vertx vertx, TestContext context, JsonObject config,
                                          ComponentProvider<T> provider, Path testDir) {
        final SharedDataLocalProxy proxy = createSharedData(vertx, provider.componentClass());
        proxy.addData(SharedDataLocalProxy.APP_DATADIR, testDir.toString());
        final T component = provider.provide(proxy);
        return VertxHelper.deploy(vertx, context, new DeploymentOptions().setConfig(config), component,
                                  TestHelper.TEST_TIMEOUT_SEC, s -> {
                final ComponentContext ctx = component.hook()
                                                      .onSuccess(component.getClass(), testDir,
                                                                 provider.componentClass().getName(), s);
                component.setup(ctx);
            });
    }

    static <T extends Component> void deployFailed(Vertx vertx, TestContext context, JsonObject config,
                                                   ComponentProvider<T> provider, Handler<Throwable> handler) {
        VertxHelper.deployFailed(vertx, context, new DeploymentOptions().setConfig(config),
                                 provider.provide(createSharedData(vertx, provider.componentClass())), handler);
    }

}

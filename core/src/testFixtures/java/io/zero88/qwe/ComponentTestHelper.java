package io.zero88.qwe;

import java.nio.file.Path;
import java.util.function.Consumer;

import io.github.zero88.utils.UUID64;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.junit5.VertxTestContext;

@SuppressWarnings({"unchecked", "rawtypes"})
public interface ComponentTestHelper {

    Path testDir();

    default String sharedKey() {
        return getClass().getName() + "--" + UUID64.random();
    }

    default SharedDataLocalProxy createSharedData(Vertx vertx) {
        return SharedDataLocalProxy.create(vertx, sharedKey());
    }

    default <T extends Component> T deploy(Vertx vertx, VertxTestContext context, JsonObject config,
                                           ComponentProvider<T> provider) {
        final T verticle = initComponent(vertx, provider);
        return VertxHelper.deploy(vertx, context, DeployContext.<T>builder()
                                                               .verticle(verticle)
                                                               .options(new DeploymentOptions().setConfig(config))
                                                               .successAsserter(id -> {
                                                                   setup(verticle, id);
                                                                   context.completeNow();
                                                               })
                                                               .build());
    }

    default <T extends Component> T deploy(Vertx vertx, TestContext context, JsonObject config,
                                           ComponentProvider<T> provider) {
        final T verticle = initComponent(vertx, provider);
        return VertxHelper.deploy(vertx, context, DeployContext.<T>builder()
                                                               .verticle(verticle)
                                                               .options(new DeploymentOptions().setConfig(config))
                                                               .successAsserter(id -> setup(verticle, id))
                                                               .build());
    }

    default <T extends Component> T initComponent(Vertx vertx, ComponentProvider<T> provider) {
        final SharedDataLocalProxy proxy = createSharedData(vertx);
        proxy.addData(SharedDataLocalProxy.APP_DATADIR, testDir().toString());
        return provider.provide(proxy);
    }

    default <T extends Component> void deployFailed(Vertx vertx, TestContext context, JsonObject config,
                                                    ComponentProvider<T> provider, Consumer<Throwable> handler) {
        VertxHelper.deploy(vertx, context, DeployContext.<T>builder()
                                                        .verticle(provider.provide(createSharedData(vertx)))
                                                        .options(new DeploymentOptions().setConfig(config))
                                                        .failedAsserter(handler)
                                                        .build());
    }

    default <T extends Component> void setup(T comp, String result) {
        comp.setup(comp.hook().onSuccess(ComponentContext.create(comp.appName(), testDir(), sharedKey(), result)));
    }

}

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
public interface PluginTestHelper {

    Path testDir();

    default String sharedKey() {
        return getClass().getName() + "--" + UUID64.random();
    }

    default SharedDataLocalProxy createSharedData(Vertx vertx) {
        return SharedDataLocalProxy.create(vertx, sharedKey());
    }

    default <T extends Plugin> T deploy(Vertx vertx, VertxTestContext context, JsonObject config,
                                        PluginProvider<T> provider) {
        final T plugin = provider.provide(createSharedData(vertx));
        return VertxHelper.deploy(vertx, context, DeployContext.<T>builder()
                                                               .verticle(preDeploy(plugin))
                                                               .options(new DeploymentOptions().setConfig(config))
                                                               .successAsserter(id -> {
                                                                   postDeploy(plugin, id);
                                                                   context.completeNow();
                                                               })
                                                               .build());
    }

    default <T extends Plugin> T deploy(Vertx vertx, TestContext context, JsonObject config,
                                        PluginProvider<T> provider) {
        final T plugin = provider.provide(createSharedData(vertx));
        return VertxHelper.deploy(vertx, context, DeployContext.<T>builder()
                                                               .verticle(preDeploy(plugin))
                                                               .options(new DeploymentOptions().setConfig(config))
                                                               .successAsserter(id -> postDeploy(plugin, id))
                                                               .build());
    }

    default <T extends Plugin> void deployFailed(Vertx vertx, TestContext context, JsonObject config,
                                                 PluginProvider<T> provider, Consumer<Throwable> handler) {
        VertxHelper.deploy(vertx, context, DeployContext.<T>builder()
                                                        .verticle(provider.provide(createSharedData(vertx)))
                                                        .options(new DeploymentOptions().setConfig(config))
                                                        .failedAsserter(handler)
                                                        .build());
    }

    default <T extends Plugin> T preDeploy(T plugin) {
        return (T) plugin.deployHook()
                         .onPostDeploy(plugin,
                                       PluginContext.createPreContext("PluginTest", plugin.pluginName(), sharedKey(),
                                                                      testDir()));
    }

    default <T extends Plugin> T postDeploy(T plugin, String deploymentId) {
        return (T) plugin.deployHook()
                         .onPostDeploy(plugin, PluginContext.createPostContext(plugin.pluginContext(), deploymentId));
    }

}

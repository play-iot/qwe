package cloud.playio.qwe;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.reactivex.annotations.NonNull;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.junit5.VertxTestContext;

@SuppressWarnings({"unchecked", "rawtypes"})
public interface PluginDeploymentHelper<TC> {

    @NonNull
    static <T extends Plugin> Set<Extension> getExtensions(PluginProvider<T> provider, SharedDataLocalProxy sharedData,
                                                           String appName, Path appDir,
                                                           Map<Class<? extends Extension>, ExtensionConfig> configMap) {
        Map<Class<? extends Extension>, ExtensionConfig> c = configMap == null ? Collections.emptyMap() : configMap;
        return provider.extensions()
                       .stream()
                       .map(cls -> BaseExtensionTest.createExt(sharedData, appName, appDir, cls, c.get(cls)))
                       .collect(Collectors.toSet());
    }

    BasePluginTest deployment();

    <P extends Plugin> P doDeploy(Vertx vertx, TC context, DeployContext<P> deployContext);

    /**
     * Deploy QWE plugin
     *
     * @param vertx       vertx
     * @param testContext vertx test context
     * @param config      plugin config
     * @param provider    plugin provider
     * @return plugin
     */
    default <P extends Plugin> P deploy(Vertx vertx, TC testContext, PluginConfig config, PluginProvider<P> provider) {
        return deploy(vertx, testContext, config, provider, null);
    }

    default <P extends Plugin> P deploy(Vertx vertx, TC testCtx, PluginConfig config, PluginProvider<P> provider,
                                        Map<Class<? extends Extension>, ExtensionConfig> extConfigMap) {
        final P plugin = preparePlugin(vertx, provider, extConfigMap);
        return doDeploy(vertx, testCtx, DeployContext.<P>builder()
                                                     .verticle(plugin)
                                                     .options(new DeploymentOptions().setConfig(config.toJson()))
                                                     .successAsserter(id -> {
                                                         plugin.deployHook()
                                                               .onPostDeploy(plugin,
                                                                             plugin.pluginContext().deployId(id));
                                                         whenSuccess(id, testCtx);
                                                     })
                                                     .build());
    }

    default <P extends Plugin> void deployFailed(Vertx vertx, TC testCtx, PluginConfig config,
                                                 PluginProvider<P> provider, Consumer<Throwable> handler) {
        final P plugin = preparePlugin(vertx, provider, null);
        doDeploy(vertx, testCtx, DeployContext.<P>builder()
                                              .verticle(plugin)
                                              .options(new DeploymentOptions().setConfig(config.toJson()))
                                              .failedAsserter(handler)
                                              .build());
    }

    default <P extends Plugin> P preparePlugin(Vertx vertx, PluginProvider<P> provider,
                                               Map<Class<? extends Extension>, ExtensionConfig> extConfigMap) {
        final BasePluginTest deployment = deployment();
        final Plugin plugin = provider.get();
        final SharedDataLocalProxy sharedData = deployment.createSharedData(vertx);
        final Set<Extension> extensions = getExtensions(provider, sharedData, deployment.appName(),
                                                        deployment.testDir(), extConfigMap);
        final PluginContext ctx = PluginContext.create(deployment.appName(), plugin.pluginName(), deployment.testDir(),
                                                       sharedData, extensions, deployment.pluginCryptoContext());
        return (P) plugin.deployHook().onPreDeploy(plugin, ctx);
    }

    default void whenSuccess(String deploymentId, TC testContext) {}

    interface Junit4 extends PluginDeploymentHelper<TestContext> {

        static Junit4 create(BasePluginTest deployment) {
            return () -> deployment;
        }

        @Override
        default <P extends Plugin> P doDeploy(Vertx vertx, TestContext context, DeployContext<P> deployContext) {
            return VertxHelper.deploy(vertx, context, deployContext);
        }

    }


    interface Junit5 extends PluginDeploymentHelper<VertxTestContext> {

        static Junit5 create(BasePluginTest deployment) {
            return () -> deployment;
        }

        @Override
        default void whenSuccess(String deploymentId, VertxTestContext testContext) {
            testContext.completeNow();
        }

        @Override
        default <P extends Plugin> P doDeploy(Vertx vertx, VertxTestContext context, DeployContext<P> deployContext) {
            return VertxHelper.deploy(vertx, context, deployContext);
        }

    }

}

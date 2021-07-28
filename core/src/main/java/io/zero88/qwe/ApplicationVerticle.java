package io.zero88.qwe;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.FileUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.PluginConfig.PluginDirConfig;
import io.zero88.qwe.event.EventBusDeliveryOption;
import io.zero88.qwe.exceptions.QWEExceptionConverter;
import io.zero88.qwe.utils.NetworkUtils;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @see Application
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class ApplicationVerticle extends AbstractVerticle
    implements Application, SharedDataLocalProxy, VerticleLifecycleHooks {

    private final Map<Class<? extends Plugin>, PluginProvider<? extends Plugin>> pluginProviders = new HashMap<>();
    private final Set<Class<? extends Extension>> extensions = new HashSet<>();
    private final ApplicationContextHolderInternal contexts = ApplicationContextHolderInternal.create();
    @Getter
    @Accessors(fluent = true)
    private QWEAppConfig appConfig;

    @Override
    public final void start() {
        logger().info("Start Application[{}]...", appName());
        this.appConfig = computeConfig(config());
        this.addData(SharedDataLocalProxy.EVENTBUS_DELIVERY_OPTION_KEY,
                     new EventBusDeliveryOption(appConfig.getDeliveryOptions()));
        this.addData(SharedDataLocalProxy.PUBLIC_IPV4_KEY, NetworkUtils.getPublicIpv4());
        this.onStart();
    }

    @Override
    public final void start(Promise<Void> promise) {
        QWEVerticle.asyncRun(vertx, promise, this::start, () -> onAsyncStart().flatMap(ignore -> installExtensions())
                                                                              .flatMap(ignore -> installPlugins())
                                                                              .onSuccess(i -> deployAllSuccess())
                                                                              .onFailure(e -> logError(e, "start"))
                                                                              .mapEmpty());
    }

    public final void stop() {
        logger().info("Stop Application[{}]...", appName());
        this.onStop();
    }

    @Override
    public final void stop(Promise<Void> promise) {
        QWEVerticle.asyncRun(vertx, promise, this::stop, () -> onAsyncStop().eventually(ignore -> uninstallPlugins())
                                                                            .eventually(ignore -> uninstallExtensions())
                                                                            .onFailure(e -> logError(e, "stop")));
    }

    @Override
    public final <T extends Plugin> Application addProvider(PluginProvider<T> provider) {
        this.pluginProviders.put(provider.pluginClass(), provider);
        return this;
    }

    @Override
    public final <E extends Extension> Application addExtension(Class<E> extensionCls) {
        this.extensions.add(extensionCls);
        return this;
    }

    @Override
    public final SharedDataLocalProxy sharedData() {
        return this;
    }

    @Override
    public final Future<Void> installPlugins() {
        final int defaultPoolSize = defaultPluginThreadPoolSize(pluginProviders.size());
        return CompositeFuture.all(pluginProviders.values()
                                                  .stream()
                                                  .map(provider -> deployPlugin(provider, defaultPoolSize))
                                                  .collect(Collectors.toList())).onSuccess(r -> {
            logger().info("Deployed [{}] plugin(s)", r.size() - r.causes().stream().filter(Objects::nonNull).count());
            pluginProviders.clear();
        }).mapEmpty();
    }

    @Override
    public final Future<Void> uninstallPlugins() {
        return CompositeFuture.join(contexts.plugins().stream().map(this::undeployPlugin).collect(Collectors.toList()))
                              .onSuccess(ar -> logger().info("Uninstalled [{}] plugin(s)", ar.size()))
                              .mapEmpty();
    }

    @Override
    public Future<Void> installExtensions() {
        return vertx.executeBlocking(h -> {
            extensions.stream().map(ReflectionClass::createObject).filter(Objects::nonNull).map(ext -> {
                logger().info("Setting up Extension[{}]...", ext.extName());
                ExtensionConfig extCfg = (ExtensionConfig) ext.computeConfig(appConfig().lookupJson(ext.configKey()));
                return ext.setup(extCfg, appName(), appConfig().dataDir(), this);
            }).forEach(contexts::addExtension);
            logger().info("Deployed [{}] extensions(s)", extensions.size());
            extensions.clear();
            h.complete();
        });
    }

    @Override
    public Future<Void> uninstallExtensions() {
        return vertx.executeBlocking(h -> {
            contexts.extensions().forEach(Extension::stop);
            h.complete();
        });
    }

    @Override
    public void onInstallCompleted(ApplicationContextHolder holder) {}

    Future<Void> undeployPlugin(PluginContext ctx) {
        return vertx.undeploy(ctx.deployId())
                    .onSuccess(unused -> logger().info("Uninstalled Plugin[{}][{}]", ctx.deployId(), ctx.pluginName()));
    }

    Future<String> deployPlugin(PluginProvider<? extends Plugin> provider, int defaultPoolSize) {
        Plugin plugin = provider.provide(this);
        logger().info("Deploying Plugin[{}]...", plugin.pluginName());
        PluginConfig pluginCfg = (PluginConfig) plugin.computeConfig(appConfig().lookupJson(plugin.configKey()));
        Path pp = null;
        if (pluginCfg instanceof PluginDirConfig) {
            pp = Paths.get(FileUtils.createFolder(appConfig().dataDir(), ((PluginDirConfig) pluginCfg).getPluginDir()));
        }
        return vertx.deployVerticle(plugin.deployHook()
                                          .onPreDeploy(plugin,
                                                       PluginContext.createPreContext(appName(), plugin.pluginName(),
                                                                                      sharedKey(), pp)),
                                    createPluginDeploymentOptions(plugin, pluginCfg, defaultPoolSize))
                    .onSuccess(id -> logger().info("Setting-up Plugin[{}][{}]...", plugin.pluginName(), id))
                    .onSuccess(id -> contexts.add(plugin.deployHook()
                                                        .onPostDeploy(plugin, PluginContext.createPostContext(
                                                            plugin.pluginContext(), id))
                                                        .pluginContext()))
                    .recover(t -> Future.failedFuture(QWEExceptionConverter.friendlyOrKeep(t)));
    }

    void deployAllSuccess() {
        try {
            //TODO: add liveness event then readiness event
            this.onInstallCompleted(contexts);
        } catch (Exception e) {
            //TODO: add failure readiness event
            logger().warn("Failed after completed plugin deployment", QWEExceptionConverter.friendlyOrKeep(e));
        }
    }

    DeploymentOptions createPluginDeploymentOptions(Plugin plugin, PluginConfig pluginConfig, int defaultPoolSize) {
        DeploymentOptions options = Optional.ofNullable(appConfig.lookup(pluginConfig.deploymentKey()))
                                            .map(s -> new DeploymentOptions(JsonObject.mapFrom(s)))
                                            .orElseGet(DeploymentOptions::new);
        int workerPoolSize = options.getWorkerPoolSize() == VertxOptions.DEFAULT_WORKER_POOL_SIZE
                             ? defaultPoolSize
                             : options.getWorkerPoolSize();
        String workerPool = Optional.ofNullable(options.getWorkerPoolName())
                                    .orElseGet(() -> DEFAULT_PLUGIN_THREAD_PREFIX + plugin.pluginName());
        options.setWorkerPoolName(workerPool).setWorkerPoolSize(workerPoolSize).setConfig(pluginConfig.toJson());
        if (logger().isDebugEnabled()) {
            logger().debug("Plugin deployment options [{}][{}]", plugin.pluginName(), options.toJson());
        }
        return options;
    }

    private void logError(Throwable e, String action) {
        logger().error("Failed to {} Application[{}]", action, appName(), e);
    }

}

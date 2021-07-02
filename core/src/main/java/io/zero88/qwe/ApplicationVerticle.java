package io.zero88.qwe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventBusDeliveryOption;
import io.zero88.qwe.exceptions.QWEExceptionConverter;
import io.zero88.qwe.utils.NetworkUtils;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @see Application
 */
@SuppressWarnings("rawtypes")
public abstract class ApplicationVerticle extends AbstractVerticle
    implements Application, SharedDataLocalProxy, VerticleLifecycleHooks {

    private final Map<Class<? extends Plugin>, PluginProvider<? extends Plugin>> providers = new HashMap<>();
    private final PluginContextLookupInternal contexts = PluginContextLookupInternal.create();
    @Getter
    @Accessors(fluent = true)
    protected QWEAppConfig appConfig;
    @Getter
    private EventBusClient eventBus;

    @Override
    public final void start() {
        logger().info("Start Application[{}]...", appName());
        this.appConfig = computeConfig(config());
        this.addData(SharedDataLocalProxy.EVENTBUS_DELIVERY_OPTION_KEY,
                     new EventBusDeliveryOption(this.appConfig.getDeliveryOptions()));
        this.addData(SharedDataLocalProxy.APP_DATADIR_KEY, this.appConfig.getDataDir());
        this.addData(SharedDataLocalProxy.PUBLIC_IPV4_KEY, NetworkUtils.getPublicIpv4());
        this.eventBus = EventBusClient.create(sharedData());
        this.onStart();
    }

    @Override
    public final void start(Promise<Void> promise) {
        QWEVerticle.asyncRun(vertx, promise, this::start, () -> onAsyncStart().flatMap(ignore -> installPlugins())
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
                                                                            .onFailure(e -> logError(e, "stop")));
    }

    @Override
    public final <T extends Plugin> Application addProvider(PluginProvider<T> provider) {
        this.providers.put(provider.pluginClass(), provider);
        return this;
    }

    @Override
    public final SharedDataLocalProxy sharedData() {
        return this;
    }

    @Override
    public final Future<Void> installPlugins() {
        final int defaultPoolSize = defaultPluginThreadPoolSize(providers.size());
        return CompositeFuture.all(providers.values()
                                            .stream()
                                            .map(provider -> deployPlugin(provider, defaultPoolSize))
                                            .collect(Collectors.toList())).onSuccess(this::deployAllSuccess).mapEmpty();
    }

    @Override
    public final Future<Void> uninstallPlugins() {
        return CompositeFuture.join(contexts.list().stream().map(this::undeployPlugin).collect(Collectors.toList()))
                              .onSuccess(ar -> logger().info("Uninstalled [{}] plugin(s)", ar.size()))
                              .mapEmpty();
    }

    @Override
    public void onInstallCompleted(PluginContextLookup lookup) { }

    Future<Void> undeployPlugin(PluginContext ctx) {
        return vertx.undeploy(ctx.deployId())
                    .onSuccess(unused -> logger().info("Uninstalled Plugin[{}][{}]", ctx.deployId(), ctx.pluginName()));
    }

    Future<String> deployPlugin(PluginProvider<? extends Plugin> provider, int defaultPoolSize) {
        final Plugin plugin = provider.provide(this);
        logger().info("Deploying Plugin[{}]...", plugin.pluginName());
        return vertx.deployVerticle(plugin, getPluginDeploymentOptions(defaultPoolSize, plugin))
                    .onSuccess(id -> deployPluginSuccess(plugin, id))
                    .recover(t -> Future.failedFuture(QWEExceptionConverter.friendlyOrKeep(t)));
    }

    @SuppressWarnings("unchecked")
    void deployPluginSuccess(Plugin plugin, String deployId) {
        logger().info("Setting-up Plugin[{}][{}]...", plugin.pluginName(), deployId);
        PluginContext def = PluginContext.create(appName(), plugin.pluginName(), appConfig.dataDir(), getSharedKey(),
                                                 deployId);
        contexts.add(plugin.setup(plugin.hook().onDeploySuccess(def)));
    }

    void deployAllSuccess(CompositeFuture r) {
        logger().info("Deployed {}/{} plugin(s)...", r.size(), this.providers.size());
        this.providers.clear();
        try {
            //TODO: add liveness event then readiness event
            this.onInstallCompleted(this.contexts);
        } catch (Exception e) {
            //TODO: add failure readiness event
            logger().warn("Failed after completed plugin deployment", QWEExceptionConverter.friendlyOrKeep(e));
        }
    }

    @SuppressWarnings("unchecked")
    DeploymentOptions getPluginDeploymentOptions(int defaultPoolSize, Plugin plugin) {
        final PluginConfig compConfig = IConfig.<PluginConfig>from(appConfig, plugin.configClass());
        DeploymentOptions options = Optional.ofNullable(appConfig.lookup(compConfig.deploymentKey()))
                                            .map(s -> new DeploymentOptions(JsonObject.mapFrom(s)))
                                            .orElseGet(DeploymentOptions::new);
        int workerPoolSize = options.getWorkerPoolSize() == VertxOptions.DEFAULT_WORKER_POOL_SIZE
                             ? defaultPoolSize
                             : options.getWorkerPoolSize();
        String workerPool = Optional.ofNullable(options.getWorkerPoolName())
                                    .orElseGet(() -> DEFAULT_PLUGIN_THREAD_PREFIX + plugin.pluginName());
        options.setWorkerPoolName(workerPool).setWorkerPoolSize(workerPoolSize).setConfig(compConfig.toJson());
        if (logger().isDebugEnabled()) {
            logger().debug("Plugin deployment options [{}][{}]", plugin.pluginName(), options.toJson());
        }
        return options;
    }

    private void logError(Throwable e, String action) {
        logger().error("Failed to {} Application[{}]", action, appName(), e);
    }

}
